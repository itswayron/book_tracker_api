package dev.wayron.book_tracker_api.security.models

data class AuthenticationRequest(
  val username: String,
  val password: String,
)
