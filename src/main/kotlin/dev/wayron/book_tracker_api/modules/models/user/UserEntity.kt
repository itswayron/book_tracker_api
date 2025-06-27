package dev.wayron.book_tracker_api.modules.models.user

import dev.wayron.book_tracker_api.utils.Base62UUIDGenerator
import jakarta.persistence.*
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Entity
@Table(name = "users")
data class UserEntity(
  @Id
  val id: String = Base62UUIDGenerator.generate(),

  val isActive: Boolean = true,
  val profileImage: String? = null,

  val username: String,
  val email: String,
  val password: String,

  @Enumerated(EnumType.STRING)
  val role: Role = Role.USER,

  val createdAt: LocalDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
  val updatedAt: LocalDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
)