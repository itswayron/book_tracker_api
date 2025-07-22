package dev.wayron.book_tracker_api.security.models.password

import dev.wayron.book_tracker_api.modules.models.user.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToOne
import java.time.LocalDateTime

@Entity
data class PasswordResetToken(
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long = 0,

  @Column(nullable = false, unique = true)
  val token: String,

  @OneToOne
  val user: User,

  @Column(nullable = false)
  val expireDate: LocalDateTime
)
