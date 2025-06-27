package dev.wayron.book_tracker_api.modules.models.security

data class RefreshTokenRequest(
  val token: String,
)
