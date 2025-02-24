package dev.wayron.book_tracker_api.reading.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import dev.wayron.book_tracker_api.book.model.Book
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

@Entity
@Table(name = "readings")
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
data class ReadingSession(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Int,

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "book_id")
  val bookId: Book,
  val progressInPercentage: Double,
  var totalProgress: Int,
  var pages: Int,
  val chapters: Int? = 0,

  @Enumerated(EnumType.STRING)
  @Column(name = "reading_state")
  val readingState: ReadingState = ReadingState.TO_READ,

  @Enumerated(EnumType.STRING)
  @Column(name = "tracking_method")
  val trackingMethod: TrackingMethod = TrackingMethod.PAGES,
  val dailyGoal: Int = 0,

  val startReadingDate: LocalDateTime = LocalDateTime.now(),
  val endReadingDate: LocalDateTime?,
  val estimatedCompletionDate: LocalDateTime?,
)