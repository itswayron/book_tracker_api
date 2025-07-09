package dev.wayron.book_tracker_api.modules.repositories.user

import dev.wayron.book_tracker_api.modules.exceptions.user.UserNotFoundException
import dev.wayron.book_tracker_api.modules.models.user.User
import org.springframework.security.core.context.SecurityContextHolder

fun UserRepository.getCurrentUser(): User {
  val username = SecurityContextHolder.getContext().authentication.name
  return this.findByUsernameField(username) ?: throw UserNotFoundException(username)
}
