package dev.wayron.book_tracker_api.security.user

import org.springframework.data.annotation.Id
import java.time.LocalDateTime

data class UserResponse(
  @Id
  val id: String,

  val username: String,
  val email: String,

  val createdAt: LocalDateTime
)