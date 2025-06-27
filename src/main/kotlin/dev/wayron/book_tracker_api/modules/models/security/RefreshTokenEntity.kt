package dev.wayron.book_tracker_api.modules.models.security

import dev.wayron.book_tracker_api.modules.models.user.UserEntity
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "refresh_tokens")
data class RefreshTokenEntity(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long = 0,

  @Column(nullable = false, columnDefinition = "TEXT")
  val token: String,
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  val user: UserEntity,

  val issuedAt: Date,
  val expiredAt: Date,
  val revoked: Boolean = false
)
