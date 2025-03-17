package dev.wayron.book_tracker_api.security.user

data class UserRequest(
  val username: String,
  val email: String,
  val password: String
)