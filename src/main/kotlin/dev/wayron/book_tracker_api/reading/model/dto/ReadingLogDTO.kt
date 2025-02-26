package dev.wayron.book_tracker_api.reading.model.dto

import java.time.LocalDateTime

data class ReadingLogDTO(
  val id: Int,
  val readingSessionId: Int,
  val bookId: Int,
  val dateOfReading: LocalDateTime,
  val quantityRead: Int
)
