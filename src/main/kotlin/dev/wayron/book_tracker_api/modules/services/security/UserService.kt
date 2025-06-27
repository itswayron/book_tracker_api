package dev.wayron.book_tracker_api.modules.services.security

import dev.wayron.book_tracker_api.modules.repositories.UserRepository
import dev.wayron.book_tracker_api.modules.models.user.UserEntity
import dev.wayron.book_tracker_api.modules.models.user.UserRequest
import dev.wayron.book_tracker_api.modules.models.user.UserResponse
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service
class UserService(
  private val repository: UserRepository,
  private val encoder: PasswordEncoder
) {
  private val logger = LoggerFactory.getLogger(UserService::class.java)

  fun createUser(request: UserRequest): UserResponse {
    logger.info("Creating user with username: ${request.username}")
    val user = UserEntity(
      username = request.username,
      email = request.email,
      password = encoder.encode(request.password)
    )
    repository.save(user)

    logger.info("User created with id: ${user.id}")
    val response = UserResponse(
      id = user.id,
      username = user.username,
      email = user.email,
      createdAt = user.createdAt
    )
    return response
  }

  fun findUserById(id: String): UserResponse {
    logger.info("Fetching user with id: $id")
    val user = repository.findById(id).orElseThrow()
    logger.info("Retrieved the book with ID: $id - Username: ${user.username}")
    val userResponse = UserResponse(
      id = user.id,
      username = user.username,
      email = user.email,
      createdAt = user.createdAt
    )
    return userResponse
  }

}