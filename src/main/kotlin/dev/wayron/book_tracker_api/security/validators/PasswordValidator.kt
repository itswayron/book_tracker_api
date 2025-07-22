package dev.wayron.book_tracker_api.security.validators

import dev.wayron.book_tracker_api.modules.validators.ValidationErrorMessages
import dev.wayron.book_tracker_api.modules.validators.Validator
import dev.wayron.book_tracker_api.security.exceptions.InvalidPasswordException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class PasswordValidator : Validator<String> {
  private val logger = LoggerFactory.getLogger(PasswordValidator::class.java)

  override fun validate(t: String) {
    logger.info("Validating password.")
    val errors = mutableListOf<String>()

    validateNotBlank(t, errors)
    validateLength(t, errors)
    validateUppercase(t, errors)
    validateLowercase(t, errors)
    validateDigit(t, errors)
    validateSpecialCharacter(t, errors)

    if (errors.isNotEmpty()) {
      logger.error("Password validation failed with errors: {}", errors)
      throw InvalidPasswordException(errors)
    }

    logger.info("Password is valid.")
  }

  private fun validateNotBlank(password: String, errors: MutableList<String>) {
    logger.debug("Validating that password is not blank.")
    if (password.isBlank()) {
      logger.error("Password is blank.")
      errors.add(ValidationErrorMessages.BLANK_PASSWORD.message)
    } else {
      logger.debug("Password is not blank.")
    }
  }

  private fun validateLength(password: String, errors: MutableList<String>) {
    logger.debug("Validating password length.")
    if (password.length < 8) {
      logger.error("Password too short.")
      errors.add(ValidationErrorMessages.PASSWORD_TOO_SHORT.message)
    } else {
      logger.debug("Password has sufficient length.")
    }
  }

  private fun validateUppercase(password: String, errors: MutableList<String>) {
    logger.debug("Validating presence of uppercase letter.")
    if (!password.any { it.isUpperCase() }) {
      logger.error("Password missing uppercase letter.")
      errors.add(ValidationErrorMessages.PASSWORD_NO_UPPERCASE.message)
    } else {
      logger.debug("Password has uppercase letter.")
    }
  }

  private fun validateLowercase(password: String, errors: MutableList<String>) {
    logger.debug("Validating presence of lowercase letter.")
    if (!password.any { it.isLowerCase() }) {
      logger.error("Password missing lowercase letter.")
      errors.add(ValidationErrorMessages.PASSWORD_NO_LOWERCASE.message)
    } else {
      logger.debug("Password has lowercase letter.")
    }
  }

  private fun validateDigit(password: String, errors: MutableList<String>) {
    logger.debug("Validating presence of digit.")
    if (!password.any { it.isDigit() }) {
      logger.error("Password missing digit.")
      errors.add(ValidationErrorMessages.PASSWORD_NO_DIGIT.message)
    } else {
      logger.debug("Password has digit.")
    }
  }

  private fun validateSpecialCharacter(password: String, errors: MutableList<String>) {
    logger.debug("Validating presence of special character.")
    val specialChars = "!@#\$%^&*()-_+=<>?/{}[]|\\~`"
    if (!password.any { specialChars.contains(it) }) {
      logger.error("Password missing special character.")
      errors.add(ValidationErrorMessages.PASSWORD_NO_SPECIAL.message)
    } else {
      logger.debug("Password has special character.")
    }
  }
}
