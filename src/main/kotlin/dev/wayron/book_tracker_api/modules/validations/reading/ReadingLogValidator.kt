package dev.wayron.book_tracker_api.modules.validations.reading

import dev.wayron.book_tracker_api.modules.exceptions.reading.InvalidReadingLogException
import dev.wayron.book_tracker_api.modules.models.reading.ReadingLog
import dev.wayron.book_tracker_api.modules.validations.Validator
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ReadingLogValidator : Validator<ReadingLog> {

  private val logger = LoggerFactory.getLogger(ReadingLogValidator::class.java)
  override fun validate(t: ReadingLog) {
    logger.info("Validating log.")
    if (t.quantityRead <= 0) {
      logger.error("Log invalid.")
      throw InvalidReadingLogException()
    }
    logger.info("Log valid.")
  }

}