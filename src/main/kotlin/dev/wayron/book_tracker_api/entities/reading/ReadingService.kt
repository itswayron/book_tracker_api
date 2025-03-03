package dev.wayron.book_tracker_api.entities.reading

import dev.wayron.book_tracker_api.entities.book.model.Book
import dev.wayron.book_tracker_api.entities.reading.model.ReadingLog
import dev.wayron.book_tracker_api.entities.reading.model.ReadingSession
import dev.wayron.book_tracker_api.entities.reading.model.dto.ReadingLogDTO
import dev.wayron.book_tracker_api.entities.reading.model.dto.ReadingSessionDTO
import dev.wayron.book_tracker_api.entities.reading.model.dto.ReadingSessionRequest
import dev.wayron.book_tracker_api.entities.reading.model.enums.ReadingState
import dev.wayron.book_tracker_api.entities.reading.model.enums.TrackingMethod
import dev.wayron.book_tracker_api.entities.reading.repositories.ReadingLogRepository
import dev.wayron.book_tracker_api.entities.reading.repositories.ReadingSessionRepository
import dev.wayron.book_tracker_api.exceptions.book.BookNotFoundException
import dev.wayron.book_tracker_api.exceptions.readingSession.ReadingSessionCompletedException
import dev.wayron.book_tracker_api.exceptions.readingSession.ReadingSessionNotFoundException
import dev.wayron.book_tracker_api.utils.Mappers
import dev.wayron.book_tracker_api.validations.Validator
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDateTime

@Service
class ReadingService(
  private val sessionRepository: ReadingSessionRepository,
  private val logRepository: ReadingLogRepository,
  private val webClient: WebClient
) {
  private val logger = LoggerFactory.getLogger(ReadingService::class.java)

  fun createReadingSession(readingSessionRequest: ReadingSessionRequest): ReadingSessionDTO {
    logger.info("Creating a ReadingSession for the book with ID: ${readingSessionRequest.bookId}")
    val book = webClient.get()
      .uri("/books/${readingSessionRequest.bookId}")
      .retrieve()
      .onStatus({ status -> status.is4xxClientError || status.is5xxServerError }) {
        logger.error("Book with ID ${readingSessionRequest.bookId} not found.")
        throw BookNotFoundException()
      }
      .bodyToMono(Book::class.java)
      .block() ?: throw BookNotFoundException()

    logger.info("Book found: '${book.title}' (ID: ${book.id}")

    val newReadingSession = ReadingSession(
      id = 0,
      book = book,
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

    Validator.validateReadingSession(newReadingSession)

    logger.info("Creating reading session for book ${book.title} (ID: ${book.id}).")
    sessionRepository.save(newReadingSession)

    logger.info("Reading session created successfully (ID: ${newReadingSession.id}).")
    return Mappers.mapReadingSessionToDTO(newReadingSession)
  }

  fun getReadingSessionById(id: Int): ReadingSession {
    logger.info("Fetching reading session with ID: $id")

    val session = sessionRepository.findById(id).orElseThrow {
      logger.error("Session with ID $id not found.")
      throw ReadingSessionNotFoundException()
    }

    logger.info("Retrieved the reading session with ID $id to the book: ${session.book.title}")
    return sessionRepository.getReferenceById(id)
  }

  fun getReadingSessionsByBookId(bookId: Int): List<ReadingSessionDTO> {
    logger.info("Fetching reading sessions for book ID: $bookId.")
    val book = getReadingSessionById(bookId)

    val list = sessionRepository.findByBookId(book.id).map { Mappers.mapReadingSessionToDTO(it) }

    logger.info("Found ${list.size} reading sessions for book ID: $bookId.")
    return list
  }

  fun addReading(readingSessionId: Int, quantityRead: Int): ReadingLogDTO {
    logger.info("Adding $quantityRead units to reading session ID: $readingSessionId")
    val session = getReadingSessionById(readingSessionId)

    if (session.readingState == ReadingState.READ) throw ReadingSessionCompletedException()
    val log = ReadingLog(
      id = 0,
      readingSession = session,
      dateOfReading = LocalDateTime.now(),
      quantityRead = quantityRead
    )

    Validator.validateReadingLog(log)

    session.addProgress(quantityRead)
    logger.info("$quantityRead units added to session ID: $readingSessionId")

    logRepository.save(log)
    logger.info("Reading log saved (session ID: $readingSessionId, quantity: $quantityRead).")

    sessionRepository.save(session)
    logger.info("Updated reading session saved (ID: $readingSessionId).")
    return Mappers.mapReadingLogToDTO(log)
  }

}