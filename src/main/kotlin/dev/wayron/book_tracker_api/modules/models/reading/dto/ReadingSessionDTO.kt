package dev.wayron.book_tracker_api.modules.models.reading.dto

import com.fasterxml.jackson.annotation.JsonFormat
import dev.wayron.book_tracker_api.modules.models.reading.enums.ReadingState
import java.time.LocalDateTime

data class ReadingSessionDTO(
  var id: Int,
  var bookId: Int,
  var bookTitle: String,
  var progressInPercentage: Double,
  var totalProgress: Int,
  var pages: Int,
  var chapters: Int?,
  var readingState: ReadingState,
  var dailyGoal: Int?,
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
  var startReadingDate: LocalDateTime,
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
  var endReadingDate: LocalDateTime?,
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
  var estimatedCompletionDate: LocalDateTime?
)