package dev.wayron.book_tracker_api.modules.models.user

import java.time.LocalDateTime

data class UserResponse(
  val id: String,
  val username: String,
  val email: String,
  val imageProfilePath: String? = null,
  val createdAt: LocalDateTime
)