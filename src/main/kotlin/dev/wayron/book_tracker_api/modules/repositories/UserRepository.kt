package dev.wayron.book_tracker_api.modules.repositories

import dev.wayron.book_tracker_api.modules.models.user.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<UserEntity, String> {
  fun findByUsername(username: String): Optional<UserEntity>
}