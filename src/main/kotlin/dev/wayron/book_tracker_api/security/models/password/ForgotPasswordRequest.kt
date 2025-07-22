package dev.wayron.book_tracker_api.security.models.password

data class ForgotPasswordRequest(
  val email: String,
)
