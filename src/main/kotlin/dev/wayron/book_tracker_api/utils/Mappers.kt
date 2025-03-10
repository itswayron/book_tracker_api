package dev.wayron.book_tracker_api.utils

import dev.wayron.book_tracker_api.modules.models.book.Book
import dev.wayron.book_tracker_api.modules.models.book.BookDTO
import dev.wayron.book_tracker_api.modules.models.reading.ReadingLog
import dev.wayron.book_tracker_api.modules.models.reading.ReadingSession
import dev.wayron.book_tracker_api.modules.models.reading.dto.ReadingLogDTO
import dev.wayron.book_tracker_api.modules.models.reading.dto.ReadingSessionDTO
import dev.wayron.book_tracker_api.modules.models.reading.dto.ReadingSessionRequest
import java.time.temporal.ChronoUnit

object Mappers {

  fun mapReadingSessionToDTO(readingSession: ReadingSession): ReadingSessionDTO {
    return ReadingSessionDTO(
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
      bookId = readingSession.book.id,
      trackingMethod = readingSession.trackingMethod,
      dailyGoal = readingSession.dailyGoal,
      startReadingDate = readingSession.startReadingDate,
      estimatedCompletionDate = readingSession.estimatedCompletionDate
    )
  }

  fun mapReadingLogToDTO(readingLog: ReadingLog): ReadingLogDTO {
    return ReadingLogDTO(
      readingLog.id,
      readingSessionId = readingLog.readingSession.id,
      bookId = readingLog.readingSession.book.id,
      dateOfReading = readingLog.dateOfReading,
      quantityRead = readingLog.quantityRead,
    )
  }

  fun mapBookToDTO(book: Book): BookDTO {
    return BookDTO(
      id = book.id,
      title = book.title,
      author = book.author,
      pages = book.pages,
      chapters = book.chapters,
    )
  }

}