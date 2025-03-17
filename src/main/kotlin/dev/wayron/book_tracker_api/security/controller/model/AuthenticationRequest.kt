package dev.wayron.book_tracker_api.security.controller.model

data class AuthenticationRequest(
  val username: String,
  val password: String,
)
