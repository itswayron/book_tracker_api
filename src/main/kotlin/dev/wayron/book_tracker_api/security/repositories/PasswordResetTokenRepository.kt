package dev.wayron.book_tracker_api.security.repositories

import dev.wayron.book_tracker_api.modules.models.user.User
import dev.wayron.book_tracker_api.security.models.password.PasswordResetToken
import org.springframework.data.jpa.repository.JpaRepository

interface PasswordResetTokenRepository : JpaRepository<PasswordResetToken, Int> {
  fun findByToken(token: String): PasswordResetToken?
  fun deleteByUser(user: User)
}
