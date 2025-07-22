package dev.wayron.book_tracker_api.security.services

import dev.wayron.book_tracker_api.modules.repositories.user.UserRepository
import dev.wayron.book_tracker_api.modules.services.EmailService
import dev.wayron.book_tracker_api.modules.validators.Validator
import dev.wayron.book_tracker_api.security.exceptions.InvalidTokenException
import dev.wayron.book_tracker_api.security.exceptions.TokenExpiredException
import dev.wayron.book_tracker_api.security.models.password.ForgotPasswordRequest
import dev.wayron.book_tracker_api.security.models.password.PasswordResetToken
import dev.wayron.book_tracker_api.security.models.password.ResetPasswordRequest
import dev.wayron.book_tracker_api.security.repositories.PasswordResetTokenRepository
import dev.wayron.book_tracker_api.utils.Base62UUIDGenerator
import dev.wayron.book_tracker_api.utils.maskLast
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class PasswordResetService(
  private val emailService: EmailService,
  private val userRepository: UserRepository,
  private val tokenRepository: PasswordResetTokenRepository,
  private val encoder: PasswordEncoder,
  private val passwordValidator: Validator<String>
) {
  private val logger = LoggerFactory.getLogger(this::class.java)

  @Transactional
  fun forgotPassword(request: ForgotPasswordRequest) {
    val email = request.email
    logger.info("Searching for email: '{}'", email)

    val user = userRepository.findByEmail(email)
    if (user == null) {
      logger.info("No user found with email: '{}', aborting password reset process.", email)
      return
    }
    logger.debug("User found for password reset: '{}'", user.username)

    val token = Base62UUIDGenerator.generate()
    val passwordResetToken = PasswordResetToken(
      token = token, user = user, expireDate = LocalDateTime.now().plusMinutes(30)
    )
    logger.trace(
      "Generated password reset token: '{}', expiring at: '{}'",
      token.maskLast(),
      passwordResetToken.expireDate
    )

    tokenRepository.deleteByUser(user)
    logger.debug("Deleted all existing tokens for user: '{}'", user.username)

    tokenRepository.save(passwordResetToken)
    logger.debug("Saved new password reset token for user: '{}'", user.username)

    emailService.sendPasswordResetEmail(email = user.email, token = token)
  }

  @Transactional
  fun resetPassword(request: ResetPasswordRequest) {
    logger.info("Received password reset attempt for token: '{}'", request.token.maskLast())

    val token = findValidTokenOrThrow(request.token)
    val user = token.user

    logger.debug("Resetting password for user: '{}'. Old hash: '{}'", user.username, user.password.maskLast())
    passwordValidator.validate(request.newPassword)
    user.passwordField = encoder.encode(request.newPassword)

    userRepository.save(user)
    logger.info("Password successfully updated for user: '{}'. New hash: '{}'", user.username, user.password.maskLast())

    tokenRepository.deleteByUser(user)
    logger.debug("Deleted token after successful password reset for user: '{}'", user.username)
  }

  private fun findValidTokenOrThrow(tokenString: String): PasswordResetToken {
    logger.trace("Searching for password reset token: '{}'", tokenString.maskLast())
    val token = tokenRepository.findByToken(tokenString)
    if (token == null) {
      logger.warn("Invalid password reset token received: '{}'", tokenString.maskLast())
      throw InvalidTokenException()
    }
    if (token.expireDate.isBefore(LocalDateTime.now())) {
      logger.warn("Expired password reset token: '{}', for user: '{}'", token.token.maskLast(), token.user.username)
      tokenRepository.delete(token)
      throw TokenExpiredException()
    }
    logger.debug("Valid password reset token found: '{}', for user: '{}'", token.token.maskLast(), token.user.username)
    return token
  }
}
