package dev.wayron.book_tracker_api.modules.services.book

import dev.wayron.book_tracker_api.modules.exceptions.book.BookNotFoundException
import dev.wayron.book_tracker_api.modules.exceptions.book.BookNotValidException
import dev.wayron.book_tracker_api.modules.models.book.Book
import dev.wayron.book_tracker_api.modules.models.book.BookRequest
import dev.wayron.book_tracker_api.modules.models.book.BookResponse
import dev.wayron.book_tracker_api.modules.models.mappers.BookMapper
import dev.wayron.book_tracker_api.modules.repositories.UserRepository
import dev.wayron.book_tracker_api.modules.repositories.book.BookRepository
import dev.wayron.book_tracker_api.modules.validators.Validator
import dev.wayron.book_tracker_api.utils.Sanitizers
import dev.wayron.book_tracker_api.utils.getCurrentUser
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.sql.Timestamp

@Service
class BookService(
  private val repository: BookRepository,
  private val validator: Validator<Book>,
  private val userRepository: UserRepository,
  private val mapper: BookMapper,
) {
  private val logger = LoggerFactory.getLogger(BookService::class.java)

  fun createBook(request: BookRequest): BookResponse {
    logger.info("Creating book with the following information: $request ")
    if (request.title == null || request.author == null || request.pages == null) {
      throw BookNotValidException(listOf())
    }
    val user = userRepository.getCurrentUser()
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
    val response = mapper.entityBookToResponse(createdBook)
    return response
  }

  fun getBooks(pageable: Pageable): Page<BookResponse> {
    logger.info("Fetching all books from the repository")
    val page = repository.findAll(pageable)
    logger.info("Retrieved ${page.content.size} books")
    val response = page.map { book -> mapper.entityBookToResponse(book) }
    return response
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

  fun updateBook(command: Pair<Int, BookRequest>): BookResponse {
    val (id, bookUpdated) = command
    logger.info("Updating the book with ID: ${id}, with the following information $bookUpdated")
    val oldBook = getBookById(id)

    var newBook = Book(
      id = 0,
      title = bookUpdated.title ?: oldBook.title,
      author = bookUpdated.author ?: oldBook.author,
      pages = bookUpdated.pages ?: oldBook.pages,
      chapters = bookUpdated.chapters ?: oldBook.chapters,
      userId = oldBook.userId,
      synopsis = bookUpdated.synopsis ?: oldBook.synopsis,
      publisher = bookUpdated.publisher ?: oldBook.publisher,
      publicationDate = bookUpdated.publicationDate ?: oldBook.publicationDate,
      language = bookUpdated.language ?: oldBook.language,
      isbn10 = bookUpdated.isbn10 ?: oldBook.isbn10,
      isbn13 = bookUpdated.isbn13 ?: oldBook.isbn13,
      typeOfMedia = bookUpdated.typeOfMedia ?: oldBook.typeOfMedia,
      genres = bookUpdated.genres ?: oldBook.genres,
    )

    newBook = Sanitizers.sanitizeBook(newBook)

    validator.validate(newBook)
    newBook.updatedAt = Timestamp(System.currentTimeMillis())
    newBook.id = id
    val response = mapper.entityBookToResponse(repository.save(newBook))
    logger.info("Book updated with the ID: ${response.id}, ${response.title} at ${newBook.updatedAt}")

    return response
  }

  fun deleteBook(id: Int) {
    logger.info("Deleting book with the ID: $id")

    val deletedBook = getBookById(id)
    logger.info("Book to be deleted with the ID: ${deletedBook.id} - Title: ${deletedBook.title}")

    repository.deleteById(deletedBook.id)
    logger.info("Book with the ID: $id has been deleted")
  }

}