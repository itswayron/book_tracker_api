package dev.wayron.book_tracker_api.reading

import dev.wayron.book_tracker_api.book.model.Book
import dev.wayron.book_tracker_api.mappers.Mappers
import dev.wayron.book_tracker_api.reading.model.*
import dev.wayron.book_tracker_api.reading.model.dto.ReadingLogDTO
import dev.wayron.book_tracker_api.reading.model.dto.ReadingSessionDTO
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

  fun createReadingSession(readingSessionRequest: ReadingSessionRequest): ReadingSessionDTO {
    logger.info("Creating a ReadingSession for the book with ID: ${readingSessionRequest.bookId}")
    val book = webClient.get()
      .uri("/books/${readingSessionRequest.bookId}")
      .retrieve()
      .bodyToMono(Book::class.java)
      .block()

    logger.info("Book found: '${book!!.title}' (ID: ${book.id}")

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

    logger.info("Creating reading session for book ${book.title} (ID: ${book.id}).")
    sessionRepository.save(newReadingSession)

    logger.info("Reading session created successfully (ID: ${newReadingSession.id}).")
    return Mappers.mapReadingSessionToDTO(newReadingSession)
  }

  fun getReadingSessionById(id: Int): ReadingSession {
    logger.info("Fetching reading session with ID: $id")
    return sessionRepository.getReferenceById(id)
  }

  fun getReadingSessionsByBookId(bookId: Int): List<ReadingSessionDTO> {
    logger.info("Fetching reading sessions for book ID: $bookId.")
    val list = sessionRepository.findAll().filter { it.bookId.id == bookId }
      .map { Mappers.mapReadingSessionToDTO(it) }
    logger.info("Found ${list.size} reading sessions for book ID: $bookId.")
    return list
  }

  fun addReading(readingSessionId: Int, quantityRead: Int): ReadingLogDTO {
    logger.info("Adding $quantityRead units to reading session ID: $readingSessionId")
    val session = getReadingSessionById(readingSessionId)

    val log = ReadingLog(
      id = 0,
      readingSession = session,
      dateOfReading = LocalDateTime.now(),
      quantityRead = quantityRead
    )

    session.addProgress(quantityRead)
    logger.info("$quantityRead units added to session ID: $readingSessionId")

    logRepository.save(log)
    logger.info("Reading log saved (session ID: $readingSessionId, quantity: $quantityRead).")

    sessionRepository.save(session)
    logger.info("Updated reading session saved (ID: $readingSessionId).")
    return Mappers.mapReadingLogToDTO(log)
  }

}