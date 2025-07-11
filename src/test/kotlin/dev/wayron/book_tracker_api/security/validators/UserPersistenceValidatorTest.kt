package dev.wayron.book_tracker_api.security.validators

import dev.wayron.book_tracker_api.modules.exceptions.user.UserNotValidException
import dev.wayron.book_tracker_api.modules.models.user.User
import dev.wayron.book_tracker_api.modules.repositories.user.UserRepository
import dev.wayron.book_tracker_api.modules.validators.ValidationErrorMessages
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class UserPersistenceValidatorTest {

  private lateinit var repository: UserRepository
  private lateinit var validator: UserPersistenceValidator

  private val validUser = User(
    id = "A001",
    usernameField = "wayron",
    email = "wayron@example.com",
    passwordField = "Strong@123",
    name = "john doe"
  )

  @BeforeEach
  fun setUp() {
    repository = mock(UserRepository::class.java)
    validator = UserPersistenceValidator(repository)
  }

  @Test
  fun `should validate when username and email are available`() {
    `when`(repository.existsByUsernameField(validUser.username)).thenReturn(false)
    `when`(repository.existsByEmail(validUser.email)).thenReturn(false)

    assertDoesNotThrow {
      validator.validateNewUser(validUser)
    }
  }

  @Test
  fun `should fail when username is already taken`() {
    `when`(repository.existsByUsernameField(validUser.username)).thenReturn(true)
    `when`(repository.existsByEmail(validUser.email)).thenReturn(false)

    val exception = assertThrows(UserNotValidException::class.java) {
      validator.validateNewUser(validUser)
    }

    assertTrue(exception.details.contains(ValidationErrorMessages.USERNAME_NOT_AVAILABLE.message))
  }

  @Test
  fun `should fail when email is already taken`() {
    `when`(repository.existsByUsernameField(validUser.username)).thenReturn(false)
    `when`(repository.existsByEmail(validUser.email)).thenReturn(true)

    val exception = assertThrows(UserNotValidException::class.java) {
      validator.validateNewUser(validUser)
    }

    assertTrue(exception.details.contains(ValidationErrorMessages.EMAIL_NOT_AVAILABLE.message))
  }

  @Test
  fun `should fail when both username and email are taken`() {
    `when`(repository.existsByUsernameField(validUser.username)).thenReturn(true)
    `when`(repository.existsByEmail(validUser.email)).thenReturn(true)

    val exception = assertThrows(UserNotValidException::class.java) {
      validator.validateNewUser(validUser)
    }

    assertTrue(exception.details.contains(ValidationErrorMessages.USERNAME_NOT_AVAILABLE.message))
    assertTrue(exception.details.contains(ValidationErrorMessages.EMAIL_NOT_AVAILABLE.message))
  }
}
