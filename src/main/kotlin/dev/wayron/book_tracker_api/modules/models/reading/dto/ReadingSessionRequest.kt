package dev.wayron.book_tracker_api.modules.models.reading.dto

import dev.wayron.book_tracker_api.modules.models.reading.enums.TrackingMethod
import java.time.LocalDateTime

data class ReadingSessionRequest(
  val trackingMethod: TrackingMethod? = TrackingMethod.PAGES,
  val dailyGoal: Int? = 0,
  val startReadingDate: LocalDateTime? = LocalDateTime.now(),
  val estimatedCompletionDate: LocalDateTime? = null,
)