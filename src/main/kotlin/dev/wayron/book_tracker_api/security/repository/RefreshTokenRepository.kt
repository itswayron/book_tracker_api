package dev.wayron.book_tracker_api.security.repository

import dev.wayron.book_tracker_api.security.controller.model.RefreshTokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshTokenEntity, Long> {
  fun findByToken(token: String): RefreshTokenEntity?
  fun findByUserIdAndRevokedFalse(userId: String): RefreshTokenEntity?
}