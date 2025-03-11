package dev.wayron.book_tracker_api.modules.validations

import dev.wayron.book_tracker_api.modules.exceptions.reading.ReadingSessionNotValidException
import dev.wayron.book_tracker_api.modules.models.reading.ReadingSession
import dev.wayron.book_tracker_api.modules.models.reading.enums.TrackingMethod
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

object ValidatorOld {

  private val logger = LoggerFactory.getLogger(ValidatorOld::class.java)

  fun validateReadingSession(session: ReadingSession) {
    logger.info("Validating reading session.")
    val errors = mutableListOf<String>()

    if (session.pages <= 0) {
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

}