package dev.wayron.book_tracker_api.modules.repositories

import dev.wayron.book_tracker_api.modules.models.user.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, String> {
  fun findByUsernameField(username: String): User?
}