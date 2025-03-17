package dev.wayron.book_tracker_api.security.controller.model

data class AuthenticationResponse(
  val accessToken: String,
  val refreshToken: String,
)
