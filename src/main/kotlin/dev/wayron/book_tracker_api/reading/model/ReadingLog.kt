package dev.wayron.book_tracker_api.reading.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.time.LocalDateTime

@Entity
@Table(name = "reading_log")
data class ReadingLog(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Int,

  @NotNull
  @ManyToOne
  @JoinColumn(name = "reading_id")
  val readingSession: ReadingSession,
  val dateOfReading: LocalDateTime = LocalDateTime.now(),

  @field:Positive(message = "The amount of reading must be positive")
  val quantityRead: Int,
)