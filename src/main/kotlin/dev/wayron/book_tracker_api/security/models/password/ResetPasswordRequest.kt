package dev.wayron.book_tracker_api.security.models.password

data class ResetPasswordRequest(
  val token: String,
  val newPassword: String,
)
