package dev.wayron.book_tracker_api.modules.validations.reading

import dev.wayron.book_tracker_api.modules.exceptions.reading.ForbiddenActionException
import dev.wayron.book_tracker_api.modules.exceptions.reading.InvalidReadingLogException
import dev.wayron.book_tracker_api.modules.models.reading.ReadingLog
import dev.wayron.book_tracker_api.modules.validations.Validator
import dev.wayron.book_tracker_api.modules.validations.user.UserAccessValidator
import dev.wayron.book_tracker_api.security.user.Role
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ReadingLogValidator : Validator<ReadingLog>, UserAccessValidator<String, String> {
  private val logger = LoggerFactory.getLogger(ReadingLogValidator::class.java)

  override fun validate(t: ReadingLog) {
    logger.info("Validating log.")

    validateQuantityRead(t.quantityRead)
    validateUserAccess(t.userId.id, t.readingSession.userId.id, t.userId.role)

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

  override fun validateUserAccess(t: String, e: String, userRole: Role) {
    logger.info("Validating user.")
    if (t != e && userRole == Role.USER) {
      logger.error("User not valid.")
      throw ForbiddenActionException()
    }
    logger.info("Valid user.")

  }

}