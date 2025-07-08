package dev.wayron.book_tracker_api.modules.validators.reading

import dev.wayron.book_tracker_api.modules.exceptions.reading.ReadingSessionNotValidException
import dev.wayron.book_tracker_api.modules.models.reading.ReadingSession
import dev.wayron.book_tracker_api.modules.models.reading.enums.TrackingMethod
import dev.wayron.book_tracker_api.modules.validators.ValidationErrorMessages
import dev.wayron.book_tracker_api.modules.validators.Validator
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ReadingSessionValidator : Validator<ReadingSession> {
  private val logger = LoggerFactory.getLogger(ReadingSessionValidator::class.java)

  override fun validate(t: ReadingSession) {
    logger.info("Validating reading session.")
    val errors = mutableListOf<String>()

    validatePages(t.pages, errors)
    validateTracking(t.chapters, t.trackingMethod, errors)
    validateDailyGoal(t.dailyGoal, errors)
    validateStartReadingDate(t.startReadingDate, errors)
    validateEstimatedCompletionDate(t, errors)
    validateEndReadingDate(t.endReadingDate, errors)

    if (errors.isNotEmpty()) {
      logger.error("Reading session invalid.")
      throw ReadingSessionNotValidException(errors)
    }

    logger.info("Valid reading session.")
  }

  private fun validatePages(pages: Int, errors: MutableList<String>) {
    logger.debug("Validating session pages: $pages")
    if (pages <= 0) {
      logger.error("Pages invalid.")
      errors.add(ValidationErrorMessages.PAGES_NOT_POSITIVE.message)
    } else {
      logger.debug("Pages valid.")
    }
  }

  private fun validateTracking(chapters: Int?, tracking: TrackingMethod, errors: MutableList<String>) {
    logger.debug("Validating session tracking: {}, chapters: {}", tracking, chapters)
    if (tracking == TrackingMethod.CHAPTERS && (chapters == null || chapters <= 0)) {
      logger.error("Invalid tracking method.")
      errors.add(ValidationErrorMessages.BOOK_HAS_NO_CHAPTERS.message)
    } else {
      logger.debug("Valid tracking method.")
    }
  }

  private fun validateDailyGoal(dailyGoal: Int, errors: MutableList<String>) {
    logger.debug("Validating session daily goal: $dailyGoal")
    if (dailyGoal < 0) {
      logger.error("Invalid daily goal.")
      errors.add(ValidationErrorMessages.NEGATIVE_DAILY_GOAL.message)
    } else {
      logger.debug("Valid daily goal.")
    }
  }

  private fun validateStartReadingDate(startDate: LocalDateTime, errors: MutableList<String>) {
    logger.debug("Validating session start reading date: {}", startDate)
    if (startDate.isAfter(LocalDateTime.now())) {
      logger.error("Invalid start date.")
      errors.add(ValidationErrorMessages.FUTURE_START_READING.message)
    } else {
      logger.debug("Valid start date.")
    }
  }

  private fun validateEstimatedCompletionDate(session: ReadingSession, errors: MutableList<String>) {
    logger.debug("Validating session estimated completion date: {}", session.estimatedCompletionDate)
    if(session.estimatedCompletionDate != null && session.startReadingDate.isAfter(session.estimatedCompletionDate)) {
      logger.error("Invalid completion date.")
      errors.add(ValidationErrorMessages.INVALID_ESTIMATED_COMPLETION_DATE.message)
    } else {
      logger.debug("Valid estimated completion date.")
    }
  }

  private fun validateEndReadingDate(endDate: LocalDateTime?, errors: MutableList<String>) {
    logger.debug("Validating session end reading date: {}", endDate)
    if (endDate != null && endDate.isAfter(LocalDateTime.now())) {
      logger.error("Invalid reading date.")
      errors.add(ValidationErrorMessages.FUTURE_END_READING.message)
    } else {
      logger.debug("Valid reading date.")
    }
  }
}
