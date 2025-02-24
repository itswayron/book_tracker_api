package dev.wayron.book_tracker_api.reading

import dev.wayron.book_tracker_api.book.model.Book
import dev.wayron.book_tracker_api.reading.model.*
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

  fun getReadingSessionById(id: Int): ReadingSession {
    return sessionRepository.getReferenceById(id)
  }

  fun getReadingSessionsByBookId(bookId: Int): List<ReadingSession> {
    return sessionRepository.findAll().filter { it.bookId.id == bookId }
  }

  fun addReading(readingSessionId: Int, quantityRead: Int): ReadingLog {
    val log = ReadingLog(
      id = 0,
      readingSession = getReadingSessionById(readingSessionId),
      dateOfReading = LocalDateTime.now(),
      quantityRead = quantityRead
    )

    val session = getReadingSessionById(readingSessionId)
    session.totalProgress += quantityRead

    sessionRepository.save(session)
    logRepository.save(log)

    return log
  }

}