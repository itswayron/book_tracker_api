package dev.wayron.book_tracker_api.reading.model.dto

import dev.wayron.book_tracker_api.reading.model.ReadingState
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
  var startReadingDate: LocalDateTime,
  var endReadingDate: LocalDateTime?,
  var estimatedCompletionDate: LocalDateTime?
)