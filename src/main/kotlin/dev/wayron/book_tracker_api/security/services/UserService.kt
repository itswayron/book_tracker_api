package dev.wayron.book_tracker_api.security.services

import dev.wayron.book_tracker_api.modules.repositories.user.UserRepository
import dev.wayron.book_tracker_api.modules.models.user.User
import dev.wayron.book_tracker_api.modules.models.user.UserRequest
import dev.wayron.book_tracker_api.modules.models.user.UserResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
  private val repository: UserRepository,
  private val encoder: PasswordEncoder
): UserDetailsService {
  private val logger = LoggerFactory.getLogger(this::class.java)

  override fun loadUserByUsername(username: String): UserDetails {
    return repository.findByUsernameField(username) ?: throw UsernameNotFoundException("User not found.")
  }

  fun createUser(request: UserRequest): UserResponse {
    logger.info("Creating user with username: ${request.username}")
    val user = User(
      usernameField = request.username,
      email = request.email,
      passwordField = encoder.encode(request.password)
    )
    repository.save(user)

    logger.info("User created with id: ${user.id}")
    val response = UserResponse(
      id = user.id,
      username = user.usernameField,
      email = user.email,
      createdAt = user.createdAt
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
      createdAt = user.createdAt
    )
    return userResponse
  }

}