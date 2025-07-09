package dev.wayron.book_tracker_api.security.services

import dev.wayron.book_tracker_api.modules.exceptions.user.UserNotFoundException
import dev.wayron.book_tracker_api.modules.models.user.User
import dev.wayron.book_tracker_api.modules.models.user.UserRequest
import dev.wayron.book_tracker_api.modules.models.user.UserResponse
import dev.wayron.book_tracker_api.modules.repositories.user.UserRepository
import dev.wayron.book_tracker_api.modules.validators.Validator
import dev.wayron.book_tracker_api.security.validators.UserPersistenceValidator
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
  private val repository: UserRepository,
  private val encoder: PasswordEncoder,
  private val validator: Validator<UserRequest>,
  private val persistenceValidator: UserPersistenceValidator,
) : UserDetailsService {
  private val logger = LoggerFactory.getLogger(this::class.java)

  override fun loadUserByUsername(username: String): UserDetails {
    return repository.findByUsernameField(username) ?: throw UserNotFoundException(username)
  }

  fun createUser(request: UserRequest): UserResponse {
    val sanitizedRequest = request.sanitized()
    logger.info("Creating user with username: ${sanitizedRequest.username}")

    validator.validate(sanitizedRequest)

    val user = User(
      usernameField = sanitizedRequest.username,
      email = sanitizedRequest.email,
      passwordField = encoder.encode(sanitizedRequest.password)
    )
    persistenceValidator.validateNewUser(user)

    repository.save(user)

    logger.info("User created with id: ${user.id}")
    val response = UserResponse(
      id = user.id,
      username = user.usernameField,
      email = user.email,
      createdAt = user.createdAt,
      imageProfilePath = user.profileImagePath,
    )
    return response
  }

  fun findUserById(id: String): UserResponse {
    logger.info("Fetching user with id: $id")
    val user = repository.findById(id).orElseThrow()
    logger.info("Retrieved the book with ID: $id - Username: ${user.usernameField}")
    val userResponse = UserResponse(
      id = user.id,
      username = user.usernameField,
      email = user.email,
      createdAt = user.createdAt,
      imageProfilePath = user.profileImagePath,
    )
    return userResponse
  }

  private fun UserRequest.sanitized(): UserRequest =
    this.copy(
      username = this.username.trim(),
      email = this.email.trim()
    )
}
