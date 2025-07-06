package dev.wayron.book_tracker_api.modules.services.book

import dev.wayron.book_tracker_api.modules.exceptions.book.BookNotFoundException
import dev.wayron.book_tracker_api.modules.exceptions.book.BookNotValidException
import dev.wayron.book_tracker_api.modules.models.book.Book
import dev.wayron.book_tracker_api.modules.models.book.BookPatch
import dev.wayron.book_tracker_api.modules.models.book.BookRequest
import dev.wayron.book_tracker_api.modules.models.book.BookResponse
import dev.wayron.book_tracker_api.modules.models.mappers.toEntity
import dev.wayron.book_tracker_api.modules.models.mappers.toResponse
import dev.wayron.book_tracker_api.modules.models.mappers.updateWith
import dev.wayron.book_tracker_api.modules.repositories.book.BookRepository
import dev.wayron.book_tracker_api.modules.repositories.findEntityByIdOrThrow
import dev.wayron.book_tracker_api.modules.repositories.user.UserRepository
import dev.wayron.book_tracker_api.modules.repositories.user.getCurrentUser
import dev.wayron.book_tracker_api.modules.validators.Validator
import dev.wayron.book_tracker_api.utils.Sanitizers
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.sql.Timestamp

@Service
class BookService(
  private val repository: BookRepository,
  private val validator: Validator<Book>,
  private val userRepository: UserRepository
) {
  private val logger = LoggerFactory.getLogger(BookService::class.java)

  fun createBook(request: BookRequest): BookResponse {
    logger.info("Creating book with the following information: $request.")

    val user = userRepository.getCurrentUser()
    val book = request.toEntity(user)

    val bookSanitized = Sanitizers.sanitizeBook(book)
    validator.validate(bookSanitized)

    val createdBook = repository.save(bookSanitized)
    logger.info("Book created with the ID: ${createdBook.id}, ${createdBook.title} at ${createdBook.createdAt}")

    val response = createdBook.toResponse()
    return response
  }

  fun getBooks(pageable: Pageable): Page<BookResponse> {
    logger.info("Fetching all books from the repository")
    val page = repository.findAll(pageable)
    logger.info("Retrieved ${page.content.size} books")
    val response = page.map { book -> book.toResponse() }
    return response
  }

  fun getBookById(id: Int): BookResponse {
    logger.info("Fetching book with the ID $id")

    val book = repository.findById(id)
      .orElseThrow {
        logger.error("Book with the ID $id not found.")
        throw BookNotFoundException()
      }

    logger.info("Retrieved book with the ID: $id - Title ${book.title}")
    val response = book.toResponse()
    return response
  }

  fun updateBook(command: Pair<Int, BookPatch>): BookResponse {
    val (id, bookUpdated) = command
    logger.info("Updating the book with ID: ${id}, with the following information $bookUpdated")
    val oldBook = repository.findEntityByIdOrThrow(id)

    var newBook = oldBook.updateWith(bookUpdated)
    newBook = Sanitizers.sanitizeBook(newBook)

    validator.validate(newBook)

    val response = repository.save(newBook).toResponse()
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
