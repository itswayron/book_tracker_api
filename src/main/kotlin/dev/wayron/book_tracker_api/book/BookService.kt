package dev.wayron.book_tracker_api.book

import dev.wayron.book_tracker_api.book.model.Book
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.sql.Timestamp

@Service
class BookService(private val repository: BookRepository) {

  private val logger = LoggerFactory.getLogger(BookService::class.java)

  fun createBook(book: Book): Book {
    logger.info("Creating book with the following information: $book ")
    return repository.save(book).apply { logger.info("Book created with the ID: $id, $title at $createdAt") }
  }

  fun getBooks(): List<Book> {
    logger.info("Fetching all books from the repository")
    return repository.findAll().apply { logger.info("Retrieved $size books") }
  }

  fun getBookById(id: Int): Book {
    logger.info("Fetching book with the ID $id")
    return repository.getReferenceById(id).apply { logger.info("Retrieved book with the ID: $id - Title $title") }
  }

  fun updateBook(command: Pair<Int, Book>): Book {
    val (id, bookUpdated) = command
    logger.info("Updating the book with ID: ${id}, with the following information $bookUpdated")
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