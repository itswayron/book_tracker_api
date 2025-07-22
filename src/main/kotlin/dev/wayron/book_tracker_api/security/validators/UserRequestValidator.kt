package dev.wayron.book_tracker_api.security.validators

import dev.wayron.book_tracker_api.modules.exceptions.user.UserNotValidException
import dev.wayron.book_tracker_api.modules.models.user.UserRequest
import dev.wayron.book_tracker_api.modules.validators.ValidationErrorMessages
import dev.wayron.book_tracker_api.modules.validators.Validator
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class UserRequestValidator : Validator<UserRequest> {
  private val logger = LoggerFactory.getLogger(this::class.java)

  override fun validate(t: UserRequest) {
    logger.info("Validating new user fields: ${t.username}")
    val errors = mutableListOf<String>()
    validateUserEmail(t.email, errors)
    validateUsername(t.username, errors)

    if (errors.isNotEmpty()) {
      logger.error("User is not valid.")
      throw UserNotValidException(
        name = t.username,
        errors = errors
      )
    }

    logger.info("Valid user fields.")
  }

  private fun validateUserEmail(email: String, errors: MutableList<String>) {
    logger.debug("Validating user email: {}", email)
    val emailRegex = Regex("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$")
    if (email.isBlank()) {
      logger.error("Email is blank.")
      errors.add(ValidationErrorMessages.BLANK_EMAIL.message)
    } else if (!email.matches(emailRegex)) {
      logger.error("Email format is invalid.")
      errors.add(ValidationErrorMessages.INVALID_EMAIL.message)
    } else {
      logger.debug("Valid email field.")
    }
  }

  private fun validateUsername(username: String, errors: MutableList<String>) {
    logger.debug("Validating username: {}", username)
    val invalidCharactersRegex = Regex("[^a-zA-Z0-9_.-]")

    if (username.isBlank()) {
      logger.error("Username is blank.")
      errors.add(ValidationErrorMessages.BLANK_USERNAME.message)
      return
    }

    if (username.length !in 3..20) {
      logger.error("Username length invalid.")
      errors.add(ValidationErrorMessages.USERNAME_LENGTH.message)
    }

    if (invalidCharactersRegex.containsMatchIn(username)) {
      logger.error("Username contains invalid characters.")
      errors.add(ValidationErrorMessages.USERNAME_INVALID_CHARS.message)
    } else {
      logger.debug("Valid username field.")
    }
  }
}
