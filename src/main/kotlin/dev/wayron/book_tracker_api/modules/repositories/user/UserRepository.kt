package dev.wayron.book_tracker_api.modules.repositories.user

import dev.wayron.book_tracker_api.modules.models.user.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, String>, UserRepositoryCustom {
  fun findByUsernameField(username: String): User?
}
