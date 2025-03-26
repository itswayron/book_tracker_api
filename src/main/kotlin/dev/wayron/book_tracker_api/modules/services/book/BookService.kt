package dev.wayron.book_tracker_api.modules.services.book

import dev.wayron.book_tracker_api.modules.exceptions.book.BookNotFoundException
import dev.wayron.book_tracker_api.modules.models.book.Book
import dev.wayron.book_tracker_api.modules.models.book.BookRequest
import dev.wayron.book_tracker_api.modules.repositories.book.BookRepository
import dev.wayron.book_tracker_api.modules.validations.Validator
import dev.wayron.book_tracker_api.security.repository.UserRepository
import dev.wayron.book_tracker_api.utils.Sanitizers
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.sql.Timestamp

@Service
class BookService(
  private val repository: BookRepository,
  private val validator: Validator<Book>,
  private val userRepository: UserRepository,
) {
  private val logger = LoggerFactory.getLogger(BookService::class.java)

  fun createBook(request: BookRequest): Book {
    logger.info("Creating book with the following information: $request ")

    val username = SecurityContextHolder.getContext().authentication.name
    val user = userRepository.findByUsername(username).orElseThrow()
    val book = Book(
      id = 0,
      title = request.title,
      author = request.author,
      pages = request.pages,
      chapters = request.chapters,
      userId = user,
      synopsis = request.synopsis,
      publisher = request.publisher,
      publicationDate = request.publicationDate,
      language = request.language,
      isbn10 = request.isbn10,
      isbn13 = request.isbn13,
      typeOfMedia = request.typeOfMedia,
      genres = request.genres,
    )
    val bookSanitized = Sanitizers.sanitizeBook(book)
    validator.validate(bookSanitized)
    val createdBook = repository.save(bookSanitized)
    logger.info("Book created with the ID: ${createdBook.id}, ${createdBook.title} at ${createdBook.createdAt}")
    return createdBook
  }

  fun getBooks(pageable: Pageable): Page<Book> {
    logger.info("Fetching all books from the repository")
    val page = repository.findAll(pageable)
    logger.info("Retrieved ${page.content.size} books")
    return page
  }

  fun getBookById(id: Int): Book {
    logger.info("Fetching book with the ID $id")

    val book = repository.findById(id)
      .orElseThrow {
        logger.error("Book with the ID $id not found.")
        throw BookNotFoundException()
      }

    logger.info("Retrieved book with the ID: $id - Title ${book.title}")
    return book
  }

  fun updateBook(command: Pair<Int, Book>): Book {
    var (id, bookUpdated) = command
    logger.info("Updating the book with ID: ${id}, with the following information $bookUpdated")
    bookUpdated = Sanitizers.sanitizeBook(bookUpdated)
    validator.validate(bookUpdated)
    bookUpdated.updatedAt = Timestamp(System.currentTimeMillis())
    bookUpdated.id = id
    return repository.save(bookUpdated).apply { logger.info("Book updated with the ID: $id, $title at $updatedAt") }
  }

  fun deleteBook(id: Int) {
    logger.info("Deleting book with the ID: $id")
    val bookDeleted = getBookById(id).apply { logger.info("Book to be deleted with the ID: $id - Title: $title") }
    repository.deleteById(bookDeleted.id)
    logger.info("Book with the ID: $id has been deleted")
  }

}