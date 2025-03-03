package dev.wayron.book_tracker_api.utils

import dev.wayron.book_tracker_api.entities.book.model.Book
import dev.wayron.book_tracker_api.entities.book.model.BookDTO
import dev.wayron.book_tracker_api.entities.reading.model.ReadingLog
import dev.wayron.book_tracker_api.entities.reading.model.ReadingSession
import dev.wayron.book_tracker_api.entities.reading.model.dto.ReadingLogDTO
import dev.wayron.book_tracker_api.entities.reading.model.dto.ReadingSessionDTO

object Mappers {

  fun mapReadingSessionToDTO(readingSession: ReadingSession): ReadingSessionDTO {
    return ReadingSessionDTO(
      id = readingSession.id,
      bookId = readingSession.bookId.id,
      bookTitle = readingSession.bookId.title,
      progressInPercentage = readingSession.progressInPercentage,
      totalProgress = readingSession.totalProgress,
      pages = readingSession.pages,
      chapters = readingSession.chapters,
      readingState = readingSession.readingState,
      dailyGoal = readingSession.dailyGoal,
      startReadingDate = readingSession.startReadingDate,
      endReadingDate = readingSession.endReadingDate,
      estimatedCompletionDate = readingSession.estimatedCompletionDate
    )
  }

  fun mapReadingLogToDTO(readingLog: ReadingLog): ReadingLogDTO {
    return ReadingLogDTO(
      readingLog.id,
      readingSessionId = readingLog.readingSession.id,
      bookId = readingLog.readingSession.bookId.id,
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