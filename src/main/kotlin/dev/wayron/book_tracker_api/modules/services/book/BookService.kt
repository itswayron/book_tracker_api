package dev.wayron.book_tracker_api.modules.services.book

import dev.wayron.book_tracker_api.modules.exceptions.book.BookNotFoundException
import dev.wayron.book_tracker_api.modules.exceptions.book.BookNotValidException
import dev.wayron.book_tracker_api.modules.models.book.Book
import dev.wayron.book_tracker_api.modules.models.book.BookRequest
import dev.wayron.book_tracker_api.modules.models.book.BookResponse
import dev.wayron.book_tracker_api.modules.models.mappers.BookMapper
import dev.wayron.book_tracker_api.modules.repositories.book.BookRepository
import dev.wayron.book_tracker_api.modules.validations.Validator
import dev.wayron.book_tracker_api.modules.validations.user.UserAccessValidator
import dev.wayron.book_tracker_api.modules.repositories.UserRepository
import dev.wayron.book_tracker_api.modules.models.user.UserEntity
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
  private val userAccessValidator: UserAccessValidator,
  private val mapper: BookMapper,
) {
  private val logger = LoggerFactory.getLogger(BookService::class.java)

  private fun getCurrentUser(): UserEntity {
    val username = SecurityContextHolder.getContext().authentication.name
    return userRepository.findByUsername(username).orElseThrow()
  }

  fun createBook(request: BookRequest): BookResponse {
    logger.info("Creating book with the following information: $request ")
    if (request.title == null || request.author == null || request.pages == null) {
      throw BookNotValidException(listOf())
    }
    val user = getCurrentUser()
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
    var (id, bookUpdated) = command
    logger.info("Updating the book with ID: ${id}, with the following information $bookUpdated")
    val user = getCurrentUser()
    val oldBook = getBookById(id)

    userAccessValidator.validate(user.id, oldBook.userId.id, user.role)

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

    val bookDeleted = getBookById(id)
    logger.info("Book to be deleted with the ID: ${bookDeleted.id} - Title: ${bookDeleted.title}")
    val user = getCurrentUser()
    userAccessValidator.validate(user.id, bookDeleted.userId.id, user.role)

    repository.deleteById(bookDeleted.id)
    logger.info("Book with the ID: $id has been deleted")
  }

}