package dev.wayron.book_tracker_api.modules.repositories

import dev.wayron.book_tracker_api.modules.models.security.RefreshTokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshTokenEntity, Long> {
  fun findByToken(token: String): RefreshTokenEntity?
  fun findByUserIdAndRevokedFalse(userId: String): RefreshTokenEntity?
}