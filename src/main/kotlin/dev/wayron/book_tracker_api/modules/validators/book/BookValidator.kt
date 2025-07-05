package dev.wayron.book_tracker_api.modules.validators.book

import dev.wayron.book_tracker_api.modules.exceptions.book.BookNotValidException
import dev.wayron.book_tracker_api.modules.models.book.Book
import dev.wayron.book_tracker_api.modules.validators.ValidationErrorMessages
import dev.wayron.book_tracker_api.modules.validators.Validator
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class BookValidator : Validator<Book> {
  private val logger = LoggerFactory.getLogger(BookValidator::class.java)

  override fun validate(t: Book) {
    logger.info("Validating book: ${t.title}")

    val errors = mutableListOf<String>()
    validateBookTitle(t.title, errors)
    validateBookAuthor(t.author, errors)
    validateBookPages(t.pages, errors)
    validateBookChapters(t.chapters, errors)

    if (errors.isNotEmpty()) {
      logger.info("Book is not valid.")
      throw (BookNotValidException(errors))
    }

    logger.info("Book ${t.title} valid.")
  }

  fun validateBookPages(pages: Int, errors: MutableList<String>) {
    logger.info("Validating the book pages: $pages.")

    if (pages <= 0) {
      logger.error("Invalid pages.")
      errors.add(ValidationErrorMessages.PAGES_NOT_POSITIVE.message)
    } else {
      logger.info("Valid number of pages.")
    }
  }

  fun validateBookChapters(chapters: Int?, errors: MutableList<String>) {
    logger.info("Validating the chapters: $chapters")

    if (chapters != null && chapters < 0) {
      logger.error("Invalid chapters.")
      errors.add(ValidationErrorMessages.NEGATIVE_CHAPTERS.message)
    } else {
      logger.info("Valid chapters.")
    }

  }

  fun validateBookAuthor(author: String, errors: MutableList<String>) {
    logger.info("Validating the book author: $author.")

    if (author.isBlank()) {
      logger.error("Invalid book author.")
      errors.add(ValidationErrorMessages.EMPTY_AUTHOR.message)
    } else {
      logger.info("Valid book author.")
    }
  }

  fun validateBookTitle(title: String, errors: MutableList<String>) {
    logger.info("Validating book title: $title")

    if (title.isBlank()) {
      logger.error("Invalid book title.")
      errors.add(ValidationErrorMessages.EMPTY_TITLE.message)
    } else {
      logger.info("Valid book title.")
    }
  }

}