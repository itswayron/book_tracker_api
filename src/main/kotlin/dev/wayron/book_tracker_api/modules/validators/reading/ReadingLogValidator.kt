package dev.wayron.book_tracker_api.modules.validators.reading

import dev.wayron.book_tracker_api.modules.exceptions.reading.InvalidReadingLogException
import dev.wayron.book_tracker_api.modules.models.reading.ReadingLog
import dev.wayron.book_tracker_api.modules.validators.Validator
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ReadingLogValidator : Validator<ReadingLog> {
  private val logger = LoggerFactory.getLogger(ReadingLogValidator::class.java)

  override fun validate(t: ReadingLog) {
    logger.info("Validating log.")

    validateQuantityRead(t.quantityRead)

    logger.info("Log valid.")
  }

  fun validateQuantityRead(quantityRead: Int) {
    logger.info("Validating quantity read.")
    if (quantityRead <= 0) {
      logger.error("Quantity read not valid.")
      throw InvalidReadingLogException()
    }
    logger.info("Valid quantity read.")
  }

}