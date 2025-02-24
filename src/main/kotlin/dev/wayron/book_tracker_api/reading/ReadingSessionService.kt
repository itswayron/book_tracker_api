package dev.wayron.book_tracker_api.reading

import dev.wayron.book_tracker_api.book.model.Book
import dev.wayron.book_tracker_api.reading.model.Reading
import dev.wayron.book_tracker_api.reading.model.ReadingRequest
import dev.wayron.book_tracker_api.reading.model.ReadingState
import dev.wayron.book_tracker_api.reading.model.TrackingMethod
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDateTime

@Service
class ReadingSessionService(
  private val sessionRepository: ReadingSessionRepository,
  private val logRepository: ReadingLogRepository,
  private val webClient: WebClient
) {
  private val logger = LoggerFactory.getLogger(ReadingSessionService::class.java)

  fun createReadingSession(readingSessionRequest: ReadingSessionRequest): ReadingSession {
    val book = webClient.get()
      .uri("/books/${readingSessionRequest.bookId}")
      .retrieve()
      .bodyToMono(Book::class.java)
      .block()

    val newReadingSession = ReadingSession(
      id = 0,
      bookId = book!!,
      progressInPercentage = 0.0,
      totalProgress = 0,
      pages = book.pages,
      chapters = book.chapters,
      readingState = ReadingState.READING,
      trackingMethod = readingSessionRequest.trackingMethod ?: TrackingMethod.PAGES,
      dailyGoal = readingSessionRequest.dailyGoal ?: 0,
      startReadingDate = readingSessionRequest.startReadingDate ?: LocalDateTime.now(),
      endReadingDate = null,
      estimatedCompletionDate = readingSessionRequest.estimatedCompletionDate,
    )
    logger.info("Creating reading $newReadingSession")
    return sessionRepository.save(newReadingSession)
  }

}