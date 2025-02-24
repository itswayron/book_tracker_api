package dev.wayron.book_tracker_api.reading.model

import java.time.LocalDateTime

data class ReadingSessionRequest(
  var bookId: Int? = null,
  val trackingMethod: TrackingMethod? = TrackingMethod.PAGES,
  val dailyGoal: Int? = 0,
  val startReadingDate: LocalDateTime? = LocalDateTime.now(),
  val estimatedCompletionDate: LocalDateTime? = null,
)