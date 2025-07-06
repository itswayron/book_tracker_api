package dev.wayron.book_tracker_api.modules.models.mappers

import dev.wayron.book_tracker_api.modules.models.book.Book
import dev.wayron.book_tracker_api.modules.models.reading.ReadingLog
import dev.wayron.book_tracker_api.modules.models.reading.ReadingSession
import dev.wayron.book_tracker_api.modules.models.reading.dto.ReadingLogResponse
import dev.wayron.book_tracker_api.modules.models.reading.dto.ReadingSessionRequest
import dev.wayron.book_tracker_api.modules.models.reading.dto.ReadingSessionResponse
import dev.wayron.book_tracker_api.modules.models.reading.enums.ReadingState
import dev.wayron.book_tracker_api.modules.models.reading.enums.TrackingMethod
import dev.wayron.book_tracker_api.modules.models.user.User
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

fun ReadingSessionRequest.toEntity(book: Book, user: User): ReadingSession = ReadingSession(
  id = 0,
  book = book,
  progressInPercentage = 0.0,
  totalProgress = 0,
  pages = book.pages,
  chapters = book.chapters,
  readingState = ReadingState.READING,
  trackingMethod = this.trackingMethod ?: TrackingMethod.PAGES,
  dailyGoal = this.dailyGoal ?: 0,
  startReadingDate = this.startReadingDate?.truncatedTo(ChronoUnit.MINUTES) ?: LocalDateTime.now()
    .truncatedTo(ChronoUnit.MINUTES),
  endReadingDate = null,
  estimatedCompletionDate = this.estimatedCompletionDate?.truncatedTo(ChronoUnit.MINUTES),
  userId = user,
)

fun ReadingLog.toResponse(): ReadingLogResponse = ReadingLogResponse(
  id = this.id,
  readingSessionId = this.readingSession.id,
  bookId = this.readingSession.book.id,
  dateOfReading = this.dateOfReading,
  quantityRead = this.quantityRead
)

fun ReadingSession.toResponse(): ReadingSessionResponse = ReadingSessionResponse(
  id = this.id,
  bookId = this.book.id,
  bookTitle = this.book.title,
  progressInPercentage = this.progressInPercentage,
  totalProgress = this.totalProgress,
  pages = this.pages,
  chapters = this.chapters,
  readingState = this.readingState,
  dailyGoal = this.dailyGoal,
  startReadingDate = this.startReadingDate,
  endReadingDate = this.endReadingDate,
  estimatedCompletionDate = this.estimatedCompletionDate
)