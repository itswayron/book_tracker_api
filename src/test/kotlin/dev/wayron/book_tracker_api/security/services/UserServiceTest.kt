package dev.wayron.book_tracker_api.security.services

import dev.wayron.book_tracker_api.modules.exceptions.user.UserNotFoundException
import dev.wayron.book_tracker_api.modules.models.user.User
import dev.wayron.book_tracker_api.modules.models.user.UserRequest
import dev.wayron.book_tracker_api.modules.repositories.user.UserRepository
import dev.wayron.book_tracker_api.modules.validators.Validator
import dev.wayron.book_tracker_api.security.validators.UserPersistenceValidator
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.*
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class UserServiceTest {

  private lateinit var repository: UserRepository
  private lateinit var encoder: PasswordEncoder
  private lateinit var validator: Validator<UserRequest>
  private lateinit var persistenceValidator: UserPersistenceValidator
  private lateinit var service: UserService

  private val request = UserRequest(
    username = "  wayron  ",
    email = "  wayron@example.com  ",
    password = "Strong@123"
  )

  private val encodedPassword = "ENCODED"

  private val user = User(
    id = "1",
    usernameField = "wayron",
    email = "wayron@example.com",
    passwordField = encodedPassword,
    createdAt = LocalDateTime.now()
  )

  @BeforeEach
  fun setup() {
    repository = mock()
    encoder = mock()
    validator = mock()
    persistenceValidator = mock()
    service = UserService(repository, encoder, validator, persistenceValidator)
  }

  @Test
  fun `should create a user successfully`() {
    `when`(encoder.encode(request.password)).thenReturn(encodedPassword)
    `when`(repository.save(any(User::class.java))).thenAnswer { it.arguments[0] }

    val sanitizedRequest = request.copy(
      username = request.username.trim(),
      email = request.email.trim()
    )

    val expectedUser = User(
      usernameField = sanitizedRequest.username,
      email = sanitizedRequest.email,
      passwordField = encodedPassword
    )

    `when`(encoder.encode(request.password)).thenReturn(encodedPassword)
    `when`(repository.save(expectedUser)).thenReturn(expectedUser)

    val response = service.createUser(request)

    verify(validator).validate(any<UserRequest>() ?: request)
    verify(persistenceValidator).validateNewUser(any<User>() ?: expectedUser)
    verify(repository).save(any<User>() ?: expectedUser)

    assertEquals("wayron", response.username)
    assertEquals("wayron@example.com", response.email)
  }

  @Test
  fun `should find user by id`() {
    `when`(repository.findById("1")).thenReturn(Optional.of(user))

    val response = service.findUserById("1")

    assertEquals(user.id, response.id)
    assertEquals(user.usernameField, response.username)
    assertEquals(user.email, response.email)
  }

  @Test
  fun `should throw exception when user by id not found`() {
    `when`(repository.findById("404")).thenReturn(Optional.empty())

    assertThrows(NoSuchElementException::class.java) {
      service.findUserById("404")
    }
  }

  @Test
  fun `should load user by username`() {
    `when`(repository.findByUsernameField("wayron")).thenReturn(user)

    val result: UserDetails = service.loadUserByUsername("wayron")

    assertEquals(user.usernameField, result.username)
    assertEquals(user.passwordField, result.password)
  }

  @Test
  fun `should throw UserNotFoundException when loading unknown user`() {
    `when`(repository.findByUsernameField("ghost")).thenReturn(null)

    assertThrows(UserNotFoundException::class.java) {
      service.loadUserByUsername("ghost")
    }
  }
}
