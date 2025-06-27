package dev.wayron.book_tracker_api.modules.models.user

data class UserRequest(
  val username: String,
  val email: String,
  val password: String
)