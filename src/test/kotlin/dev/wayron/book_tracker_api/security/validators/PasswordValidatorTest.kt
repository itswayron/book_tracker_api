package dev.wayron.book_tracker_api.security.validators

import dev.wayron.book_tracker_api.modules.validators.ValidationErrorMessages
import dev.wayron.book_tracker_api.security.exceptions.InvalidPasswordException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PasswordValidatorTest {

  private lateinit var validator: PasswordValidator

  @BeforeEach
  fun setUp() {
    validator = PasswordValidator()
  }

  @Test
  fun `should pass for a valid password`() {
    val validPassword = "Strong@123"
    assertDoesNotThrow {
      validator.validate(validPassword)
    }
  }

  @Test
  fun `should fail when password is blank`() {
    val blankPassword = "   "
    val exception = assertThrows(InvalidPasswordException::class.java) {
      validator.validate(blankPassword)
    }
    assertTrue(exception.details.contains(ValidationErrorMessages.BLANK_PASSWORD.message))
  }

  @Test
  fun `should fail when password is too short`() {
    val shortPassword = "S@1a"
    val exception = assertThrows(InvalidPasswordException::class.java) {
      validator.validate(shortPassword)
    }
    assertTrue(exception.details.contains(ValidationErrorMessages.PASSWORD_TOO_SHORT.message))
  }

  @Test
  fun `should fail when password has no uppercase`() {
    val noUppercase = "strong@123"
    val exception = assertThrows(InvalidPasswordException::class.java) {
      validator.validate(noUppercase)
    }
    assertTrue(exception.details.contains(ValidationErrorMessages.PASSWORD_NO_UPPERCASE.message))
  }

  @Test
  fun `should fail when password has no lowercase`() {
    val noLowercase = "STRONG@123"
    val exception = assertThrows(InvalidPasswordException::class.java) {
      validator.validate(noLowercase)
    }
    assertTrue(exception.details.contains(ValidationErrorMessages.PASSWORD_NO_LOWERCASE.message))
  }

  @Test
  fun `should fail when password has no digit`() {
    val noDigit = "Strong@abc"
    val exception = assertThrows(InvalidPasswordException::class.java) {
      validator.validate(noDigit)
    }
    assertTrue(exception.details.contains(ValidationErrorMessages.PASSWORD_NO_DIGIT.message))
  }

  @Test
  fun `should fail when password has no special character`() {
    val noSpecial = "Strong1234"
    val exception = assertThrows(InvalidPasswordException::class.java) {
      validator.validate(noSpecial)
    }
    assertTrue(exception.details.contains(ValidationErrorMessages.PASSWORD_NO_SPECIAL.message))
  }

  @Test
  fun `should collect multiple errors if several rules fail`() {
    val badPassword = "short"
    val exception = assertThrows(InvalidPasswordException::class.java) {
      validator.validate(badPassword)
    }

    val details = exception.details
    assertTrue(details.contains(ValidationErrorMessages.PASSWORD_TOO_SHORT.message))
    assertTrue(details.contains(ValidationErrorMessages.PASSWORD_NO_UPPERCASE.message))
    assertTrue(details.contains(ValidationErrorMessages.PASSWORD_NO_DIGIT.message))
    assertTrue(details.contains(ValidationErrorMessages.PASSWORD_NO_SPECIAL.message))
  }
}
