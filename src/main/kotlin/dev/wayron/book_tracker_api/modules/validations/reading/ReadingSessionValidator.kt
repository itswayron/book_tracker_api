package dev.wayron.book_tracker_api.modules.validations.reading

import dev.wayron.book_tracker_api.modules.exceptions.reading.ReadingSessionNotValidException
import dev.wayron.book_tracker_api.modules.models.reading.ReadingSession
import dev.wayron.book_tracker_api.modules.models.reading.enums.TrackingMethod
import dev.wayron.book_tracker_api.modules.validations.ValidationErrorMessages
import dev.wayron.book_tracker_api.modules.validations.Validator
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
    validateEndReadingDate(t.endReadingDate, errors)

    if (errors.isNotEmpty()) {
      logger.error("Reading session invalid.")
      throw ReadingSessionNotValidException(errors)
    }

    logger.info("Valid reading session.")
  }

  fun validatePages(pages: Int, errors: MutableList<String>) {
    logger.info("Validating session pages: $pages")

    if (pages <= 0) {
      logger.error("Pages invalid.")
      errors.add(ValidationErrorMessages.PAGES_NOT_POSITIVE.message)
    } else {
      logger.info("Pages valid.")
    }

  }

  fun validateTracking(chapters: Int?, tracking: TrackingMethod, errors: MutableList<String>) {
    logger.info("Validating session tracking: $tracking, chapters: $chapters")

    if (tracking == TrackingMethod.CHAPTERS && (chapters == null || chapters <= 0)) {
      logger.error("Invalid tracking method.")
      errors.add(ValidationErrorMessages.BOOK_HAS_NO_CHAPTERS.message)
    } else {
      logger.info("Valid tracking method.")
    }

  }

  fun validateDailyGoal(dailyGoal: Int, errors: MutableList<String>) {
    logger.info("Validating session daily goal: $dailyGoal")

    if (dailyGoal < 0) {
      logger.error("Invalid daily goal.")
      errors.add(ValidationErrorMessages.NEGATIVE_DAILY_GOAL.message)
    } else {
      logger.info("Valid daily goal.")
    }

  }

  fun validateStartReadingDate(startDate: LocalDateTime, errors: MutableList<String>) {
    logger.info("Validating session start reading date: $startDate")

    if (startDate.isAfter(LocalDateTime.now())) {
      logger.error("Invalid start date.")
      errors.add(ValidationErrorMessages.FUTURE_START_READING.message)
    } else {
      logger.info("Valid start date.")
    }

  }

  fun validateEndReadingDate(endDate: LocalDateTime?, errors: MutableList<String>) {
    logger.info("Validating session end reading date: $endDate")

    if (endDate != null && endDate.isAfter(LocalDateTime.now())) {
      logger.error("Invalid reading date.")
      errors.add(ValidationErrorMessages.FUTURE_END_READING.message)
    } else {
      logger.info("Valid reading date.")
    }

  }

}