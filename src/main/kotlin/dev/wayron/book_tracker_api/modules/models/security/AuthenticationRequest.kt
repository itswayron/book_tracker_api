package dev.wayron.book_tracker_api.modules.models.security

data class AuthenticationRequest(
  val username: String,
  val password: String,
)
