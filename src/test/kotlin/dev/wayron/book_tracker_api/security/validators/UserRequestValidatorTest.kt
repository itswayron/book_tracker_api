package dev.wayron.book_tracker_api.security.validators

import dev.wayron.book_tracker_api.modules.exceptions.user.UserNotValidException
import dev.wayron.book_tracker_api.modules.models.user.UserRequest
import dev.wayron.book_tracker_api.modules.validators.ValidationErrorMessages.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserRequestValidatorTest {

  private lateinit var validator: UserRequestValidator

  @BeforeEach
  fun setup() {
    validator = UserRequestValidator()
  }

  @Test
  fun `should validate valid user request`() {
    val request = UserRequest(
      username = "wayron_dev",
      email = "wayron@example.com",
      password = "Strong@123",
      name = "John Doe"
    )

    assertDoesNotThrow {
      validator.validate(request)
    }
  }

  @Test
  fun `should fail for blank email`() {
    val request = UserRequest(
      username = "wayron",
      name = "John doe",
      email = "",
      password = "Strong@123"
    )

    val ex = assertThrows(UserNotValidException::class.java) {
      validator.validate(request)
    }

    assertTrue(ex.details.contains(BLANK_EMAIL.message))
  }

  @Test
  fun `should fail for invalid email`() {
    val request = UserRequest(
      username = "wayron",
      name = "John doe",
      email = "invalid-email",
      password = "Strong@123"
    )

    val ex = assertThrows(UserNotValidException::class.java) {
      validator.validate(request)
    }

    assertTrue(ex.details.contains(INVALID_EMAIL.message))
  }

  @Test
  fun `should fail for blank username`() {
    val request = UserRequest(
      username = "",
      name = "John doe",
      email = "wayron@email.com",
      password = "Strong@123"
    )

    val ex = assertThrows(UserNotValidException::class.java) {
      validator.validate(request)
    }

    assertTrue(ex.details.contains(BLANK_USERNAME.message))
  }

  @Test
  fun `should fail for short username and invalid characters`() {
    val request = UserRequest(
      username = "a!",
      name = "John doe",
      email = "valid@email.com",
      password = "Strong@123"
    )

    val ex = assertThrows(UserNotValidException::class.java) {
      validator.validate(request)
    }

    assertTrue(ex.details.contains(USERNAME_LENGTH.message))
    assertTrue(ex.details.contains(USERNAME_INVALID_CHARS.message))
  }

  @Test
  fun `should fail for weak password`() {
    val request = UserRequest(
      username = "wayron",
      name = "John doe",
      email = "wayron@email.com",
      password = "abc"
    )

    val ex = assertThrows(UserNotValidException::class.java) {
      validator.validate(request)
    }

    assertTrue(ex.details.contains(PASSWORD_TOO_SHORT.message))
    assertTrue(ex.details.contains(PASSWORD_NO_UPPERCASE.message))
    assertTrue(ex.details.contains(PASSWORD_NO_DIGIT.message))
    assertTrue(ex.details.contains(PASSWORD_NO_SPECIAL.message))
  }

  @Test
  fun `should fail for password with no special character`() {
    val request = UserRequest(
      username = "wayron",
      name = "John doe",
      email = "wayron@email.com",
      password = "Password1"
    )

    val ex = assertThrows(UserNotValidException::class.java) {
      validator.validate(request)
    }

    assertTrue(ex.details.contains(PASSWORD_NO_SPECIAL.message))
  }
}
