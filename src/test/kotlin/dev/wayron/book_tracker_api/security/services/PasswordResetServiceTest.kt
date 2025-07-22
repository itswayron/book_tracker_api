/*import dev.wayron.book_tracker_api.modules.models.user.User
import dev.wayron.book_tracker_api.modules.repositories.user.UserRepository
import dev.wayron.book_tracker_api.modules.services.EmailService
import dev.wayron.book_tracker_api.modules.validators.Validator
import dev.wayron.book_tracker_api.security.exceptions.InvalidTokenException
import dev.wayron.book_tracker_api.security.exceptions.TokenExpiredException
import dev.wayron.book_tracker_api.security.models.password.ForgotPasswordRequest
import dev.wayron.book_tracker_api.security.models.password.PasswordResetToken
import dev.wayron.book_tracker_api.security.models.password.ResetPasswordRequest
import dev.wayron.book_tracker_api.security.repositories.PasswordResetTokenRepository
import dev.wayron.book_tracker_api.security.services.PasswordResetService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime
import kotlin.test.assertEquals

class PasswordResetServiceTest {

  private lateinit var emailService: EmailService
  private lateinit var userRepository: UserRepository
  private lateinit var tokenRepository: PasswordResetTokenRepository
  private lateinit var passwordEncoder: PasswordEncoder
  private lateinit var passwordValidator: Validator<String>
  private lateinit var service: PasswordResetService

  private val user = User(
    id = "userId",
    usernameField = "johndoe",
    name = "John Doe",
    email = "john.doe@example.com",
    passwordField = "oldHashedPassword",
    profileImagePath = null,
    createdAt = LocalDateTime.now(),
    updatedAt = LocalDateTime.now()
  )

  @BeforeEach
  fun setup() {
    emailService = mockk(relaxed = true) // relaxed para não precisar mockar tudo
    userRepository = mockk()
    tokenRepository = mockk()
    passwordEncoder = mockk()
    passwordValidator = mockk()

    service = PasswordResetService(
      emailService, userRepository, tokenRepository, passwordEncoder, passwordValidator
    )
  }

  @Test
  fun `forgotPassword sends email and creates token when user exists`() {
    every { userRepository.findByEmail("john.doe@example.com") } returns user
    every { tokenRepository.deleteByUser(user) } just Runs
    every { tokenRepository.save(any()) } just Runs
    every { emailService.sendPasswordResetEmail(any(), any()) } just Runs

    service.forgotPassword(ForgotPasswordRequest(email = "john.doe@example.com"))

    verify(exactly = 1) { userRepository.findByEmail("john.doe@example.com") }
    verify(exactly = 1) { tokenRepository.deleteByUser(user) }
    verify(exactly = 1) { tokenRepository.save(match { it.user == user }) }
    verify(exactly = 1) { emailService.sendPasswordResetEmail(eq("john.doe@example.com"), match { it.isNotBlank() }) }
  }

  @Test
  fun `forgotPassword does nothing when user not found`() {
    every { userRepository.findByEmail("unknown@example.com") } returns null

    service.forgotPassword(ForgotPasswordRequest(email = "unknown@example.com"))

    verify(exactly = 1) { userRepository.findByEmail("unknown@example.com") }
    verify(exactly = 0) { tokenRepository.deleteByUser(any()) }
    verify(exactly = 0) { tokenRepository.save(any()) }
    verify(exactly = 0) { emailService.sendPasswordResetEmail(any(), any()) }
  }

  @Test
  fun `resetPassword updates password and deletes token when token valid`() {
    val tokenString = "validToken"
    val token = PasswordResetToken(token = tokenString, user = user, expireDate = LocalDateTime.now().plusMinutes(10))

    every { tokenRepository.findByToken(tokenString) } returns token
    every { passwordValidator.validate("newPassword123") } just Runs
    every { passwordEncoder.encode("newPassword123") } returns "hashedNewPassword"
    every { userRepository.save(user) } just Runs
    every { tokenRepository.deleteByUser(user) } just Runs

    val request = ResetPasswordRequest(token = tokenString, newPassword = "newPassword123")

    service.resetPassword(request)

    verify(exactly = 1) { passwordValidator.validate("newPassword123") }
    assertEquals("hashedNewPassword", user.passwordField)
    verify(exactly = 1) { userRepository.save(user) }
    verify(exactly = 1) { tokenRepository.deleteByUser(user) }
  }

  @Test
  fun `resetPassword throws InvalidTokenException when token not found`() {
    val tokenString = "invalidToken"
    every { tokenRepository.findByToken(tokenString) } returns null

    val request = ResetPasswordRequest(token = tokenString, newPassword = "password")

    assertThrows<InvalidTokenException> {
      service.resetPassword(request)
    }
  }

  @Test
  fun `resetPassword throws TokenExpiredException when token expired`() {
    val tokenString = "expiredToken"
    val expiredToken =
      PasswordResetToken(token = tokenString, user = user, expireDate = LocalDateTime.now().minusMinutes(1))

    every { tokenRepository.findByToken(tokenString) } returns expiredToken
    every { tokenRepository.delete(expiredToken) } just Runs

    val request = ResetPasswordRequest(token = tokenString, newPassword = "password")

    assertThrows<TokenExpiredException> {
      service.resetPassword(request)
    }

    verify(exactly = 1) { tokenRepository.delete(expiredToken) }
  }
}
*/