package dev.wayron.book_tracker_api.modules.models.reading.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class ReadingLogResponse(
  val id: Int,
  val readingSessionId: Int,
  val bookId: Int,
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
  val dateOfReading: LocalDateTime,
  val quantityRead: Int
)