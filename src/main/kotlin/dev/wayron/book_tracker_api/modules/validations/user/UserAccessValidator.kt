package dev.wayron.book_tracker_api.modules.validations.user

import dev.wayron.book_tracker_api.modules.exceptions.reading.ForbiddenActionException
import dev.wayron.book_tracker_api.modules.models.user.Role
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class UserAccessValidator {
  private val logger = LoggerFactory.getLogger(UserAccessValidator::class.java)

  fun validate(registeredUserId: String, actionUserId: String, userRole: Role) {
    logger.info("Validating user.")
    if (registeredUserId != actionUserId && userRole == Role.USER) {
      logger.error("User not valid.")
      throw ForbiddenActionException()
    }
    logger.info("Valid user.")

  }

}