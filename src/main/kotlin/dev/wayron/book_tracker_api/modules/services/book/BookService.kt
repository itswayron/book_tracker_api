package dev.wayron.book_tracker_api.modules.services.book

import dev.wayron.book_tracker_api.modules.exceptions.book.BookNotFoundException
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
import dev.wayron.book_tracker_api.modules.services.ImageService
import dev.wayron.book_tracker_api.modules.validators.Validator
import dev.wayron.book_tracker_api.utils.Sanitizers
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.sql.Timestamp

@Service
class BookService(
  private val repository: BookRepository,
  private val validator: Validator<Book>,
  private val userRepository: UserRepository,
  private val imageService: ImageService,
) {
  private val logger = LoggerFactory.getLogger(BookService::class.java)

  fun createBook(request: BookRequest): BookResponse {
    logger.info("Creating book for current user. Payload: {}", request)

    val user = userRepository.getCurrentUser()
    val book = request.toEntity(user)

    val bookSanitized = Sanitizers.sanitizeBook(book)
    logger.debug("Book sanitized: {}", bookSanitized)
    validator.validate(bookSanitized)

    val createdBook = repository.save(bookSanitized)
    logger.info("Book created with ID={}, title='{}', at {}", createdBook.id, createdBook.title, createdBook.createdAt)

    val response = createdBook.toResponse()
    return response
  }

  fun getBooks(pageable: Pageable): Page<BookResponse> {
    logger.info("Fetching paginated books from repository.")
    val page = repository.findAll(pageable)
    logger.info("Retrieved {} books.", page.content.size)
    val response = page.map { book -> book.toResponse() }
    return response
  }

  fun getBookById(id: Int): BookResponse {
    logger.info("Fetching book by ID={}", id)

    val book = repository.findById(id)
      .orElseThrow {
        logger.error("Book with ID={} not found.", id)
        throw BookNotFoundException()
      }

    logger.info("Book found: ID={}, title='{}'", id, book.title)
    val response = book.toResponse()
    return response
  }

  fun updateBook(command: Pair<Int, BookPatch>): BookResponse {
    val (id, bookUpdated) = command
    logger.info("Updating book ID={} with patch: {}", id, bookUpdated)

    val oldBook = repository.findEntityByIdOrThrow(id)
    logger.debug("Original book data: {}", oldBook)

    var newBook = oldBook.updateWith(bookUpdated)
    newBook = Sanitizers.sanitizeBook(newBook)
    logger.debug("Sanitized updated book: {}", newBook)

    validator.validate(newBook)

    val response = repository.save(newBook).toResponse()
    logger.info("Book updated: ID={}, title='{}', at {}", response.id, response.title, newBook.updatedAt)

    return response
  }

  fun deleteBook(id: Int) {
    logger.info("Deleting book with ID={}", id)

    val deletedBook = repository.findEntityByIdOrThrow(id)
    logger.info("Book to delete found: ID={}, title='{}'", deletedBook.id, deletedBook.title)

    deletedBook.deleteImageIfExists()

    repository.deleteById(deletedBook.id)
    logger.info("Book deleted successfully. ID={}", id)
  }

  fun uploadCover(id: Int, coverFile: MultipartFile) {
    logger.info("Uploading cover for book ID={}", id)
    val book = repository.findEntityByIdOrThrow(id)

    book.deleteImageIfExists()
    val imagePath = imageService.saveImage("book", book.id.toString(), coverFile)
    logger.debug("New image saved at path={}", imagePath)

    book.coverPath = imagePath
    book.updatedAt = Timestamp(System.currentTimeMillis())

    repository.save(book)
    logger.info("Book cover updated successfully for book ID={}", book.id)
  }

  private fun Book.deleteImageIfExists() {
    this.coverPath?.let { path ->
      try {
        logger.debug("Attempting to delete existing cover image at path={}", path)
        imageService.deleteImage(path)
        logger.info("Cover image deleted successfully. Path={}", path)
      } catch (ex: Exception) {
        logger.warn("Failed to delete cover image. Path={}, Reason={}", path, ex.message)
      }
    } ?: logger.debug("No existing cover image to delete for book ID={}", this.id)
  }
}
