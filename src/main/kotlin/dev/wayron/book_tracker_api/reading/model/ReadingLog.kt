package dev.wayron.book_tracker_api.reading.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
data class ReadingLog(
  @Id
  val id:Int,
  val readingId: Int,
  val dateOfReading: LocalDateTime = LocalDateTime.now(),
  val quantityRead: Int,
)