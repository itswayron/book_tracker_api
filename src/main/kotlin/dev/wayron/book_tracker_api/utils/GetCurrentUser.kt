package dev.wayron.book_tracker_api.utils

import dev.wayron.book_tracker_api.modules.models.user.User
import dev.wayron.book_tracker_api.modules.repositories.UserRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.security.core.context.SecurityContextHolder

fun UserRepository.getCurrentUser(): User {
  val username = SecurityContextHolder.getContext().authentication.name
  return this.findByUsernameField(username) ?: throw EntityNotFoundException("User $username not found.")
}