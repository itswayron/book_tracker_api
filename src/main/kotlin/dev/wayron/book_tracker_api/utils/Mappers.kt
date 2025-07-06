package dev.wayron.book_tracker_api.utils

import dev.wayron.book_tracker_api.modules.models.book.Book
import dev.wayron.book_tracker_api.modules.models.book.BookResponse
import dev.wayron.book_tracker_api.modules.models.reading.ReadingLog
import dev.wayron.book_tracker_api.modules.models.reading.ReadingSession
import dev.wayron.book_tracker_api.modules.models.reading.dto.ReadingLogResponse
import dev.wayron.book_tracker_api.modules.models.reading.dto.ReadingSessionResponse
import dev.wayron.book_tracker_api.modules.models.reading.dto.ReadingSessionRequest
import java.time.temporal.ChronoUnit

object Mappers {

  fun mapReadingSessionToDTO(readingSession: ReadingSession): ReadingSessionResponse {
    return ReadingSessionResponse(
      id = readingSession.id,
      bookId = readingSession.book.id,
      bookTitle = readingSession.book.title,
      progressInPercentage = readingSession.progressInPercentage,
      totalProgress = readingSession.totalProgress,
      pages = readingSession.pages,
      chapters = readingSession.chapters,
      readingState = readingSession.readingState,
      dailyGoal = readingSession.dailyGoal,
      startReadingDate = readingSession.startReadingDate.truncatedTo(ChronoUnit.MINUTES),
      endReadingDate = readingSession.endReadingDate?.truncatedTo(ChronoUnit.MINUTES),
      estimatedCompletionDate = readingSession.estimatedCompletionDate?.truncatedTo(ChronoUnit.MINUTES)
    )
  }

  fun mapReadingSessionToRequest(readingSession: ReadingSession): ReadingSessionRequest {
    return ReadingSessionRequest(
      trackingMethod = readingSession.trackingMethod,
      dailyGoal = readingSession.dailyGoal,
      startReadingDate = readingSession.startReadingDate,
      estimatedCompletionDate = readingSession.estimatedCompletionDate
    )
  }

  fun mapReadingLogToDTO(readingLog: ReadingLog): ReadingLogResponse {
    return ReadingLogResponse(
      readingLog.id,
      readingSessionId = readingLog.readingSession.id,
      bookId = readingLog.readingSession.book.id,
      dateOfReading = readingLog.dateOfReading,
      quantityRead = readingLog.quantityRead,
    )
  }

  fun mapBookToDTO(book: Book): BookResponse {
    return BookResponse(
      id = book.id,
      title = book.title,
      author = book.author,
      pages = book.pages,
      chapters = book.chapters,
    )
  }

}