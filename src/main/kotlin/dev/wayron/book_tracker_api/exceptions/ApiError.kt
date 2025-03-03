package dev.wayron.book_tracker_api.exceptions

import java.time.LocalDateTime

data class ApiError(
  val timestamp: LocalDateTime = LocalDateTime.now(),
  val status: Int,
  val error: String,
  val message: String,
  val path: String,
  val details: List<String?> = emptyList()
)
