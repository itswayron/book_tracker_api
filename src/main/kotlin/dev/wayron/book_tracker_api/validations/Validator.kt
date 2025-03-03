package dev.wayron.book_tracker_api.validations

import dev.wayron.book_tracker_api.entities.book.model.Book
import dev.wayron.book_tracker_api.exceptions.book.BookNotValidException
import dev.wayron.book_tracker_api.exceptions.logs.InvalidReadingLogException
import dev.wayron.book_tracker_api.exceptions.readingSession.ReadingSessionNotValidException
import dev.wayron.book_tracker_api.entities.reading.model.ReadingLog
import dev.wayron.book_tracker_api.entities.reading.model.ReadingSession
import dev.wayron.book_tracker_api.entities.reading.model.enums.TrackingMethod
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

object Validator {

  private val logger = LoggerFactory.getLogger(Validator::class.java)

  fun validateReadingSession(session: ReadingSession) {
    logger.info("Validating reading session.")
    val errors = mutableListOf<String>()

    if(session.pages <= 0) {
      errors.add(ValidationErrorMessages.PAGES_NOT_POSITIVE.message)
    }
    if (session.trackingMethod == TrackingMethod.CHAPTERS && (session.book.chapters == null || session.book.chapters == 0)) {
      errors.add(ValidationErrorMessages.BOOK_HAS_NO_CHAPTERS.message)
    }
    if (session.dailyGoal < 0) {
      errors.add(ValidationErrorMessages.NEGATIVE_DAILY_GOAL.message)
    }
    if (session.startReadingDate.isAfter(LocalDateTime.now())) {
      errors.add(ValidationErrorMessages.FUTURE_START_READING.message)
    }
    if (session.endReadingDate != null && session.endReadingDate!!.isAfter(LocalDateTime.now())) {
      errors.add(ValidationErrorMessages.FUTURE_END_READING.message)
    }


    if (errors.isNotEmpty()) {
      logger.error("Reading session invalid.")
      throw ReadingSessionNotValidException(errors)
    }

    logger.info("Valid reading session.")
  }

  fun validateReadingLog(log: ReadingLog) {
    logger.info("Validating log.")
    if (log.quantityRead <= 0) {
      logger.error("Log invalid.")
      throw InvalidReadingLogException()
    }
    logger.info("Log valid.")
  }

  fun validateBook(book: Book) {
    logger.info("Validating book: ${book.title}.")

    val errors = mutableListOf<String>()
    if (book.title.isBlank()) errors.add(ValidationErrorMessages.EMPTY_TITLE.message)
    if (book.author.isBlank()) errors.add(ValidationErrorMessages.EMPTY_AUTHOR.message)
    if (book.pages <= 0) errors.add(ValidationErrorMessages.PAGES_NOT_POSITIVE.message)
    if (book.chapters != null && book.chapters!! < 0) errors.add(ValidationErrorMessages.NEGATIVE_CHAPTERS.message)

    if (errors.isNotEmpty()) {
      logger.info("Book is not valid.")
      throw BookNotValidException(errors)
    }

    logger.info("Book ${book.title} valid.")
  }
}