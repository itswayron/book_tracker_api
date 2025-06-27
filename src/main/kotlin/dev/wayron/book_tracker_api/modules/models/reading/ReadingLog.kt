package dev.wayron.book_tracker_api.modules.models.reading

import com.fasterxml.jackson.annotation.JsonFormat
import dev.wayron.book_tracker_api.modules.models.user.User
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  var userId: User,

  @NotNull
  @ManyToOne
  @JoinColumn(name = "reading_id")
  val readingSession: ReadingSession,
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
  val dateOfReading: LocalDateTime = LocalDateTime.now(),

  @field:Positive(message = "The amount of reading must be positive")
  val quantityRead: Int,
)