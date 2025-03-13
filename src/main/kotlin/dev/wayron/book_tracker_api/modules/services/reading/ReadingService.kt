package dev.wayron.book_tracker_api.modules.services.reading

import dev.wayron.book_tracker_api.modules.exceptions.reading.ReadingSessionCompletedException
import dev.wayron.book_tracker_api.modules.exceptions.reading.ReadingSessionNotFoundException
import dev.wayron.book_tracker_api.modules.models.reading.ReadingLog
import dev.wayron.book_tracker_api.modules.models.reading.ReadingSession
import dev.wayron.book_tracker_api.modules.models.reading.dto.ReadingLogResponse
import dev.wayron.book_tracker_api.modules.models.reading.dto.ReadingSessionResponse
import dev.wayron.book_tracker_api.modules.models.reading.dto.ReadingSessionRequest
import dev.wayron.book_tracker_api.modules.models.reading.enums.ReadingState
import dev.wayron.book_tracker_api.modules.models.reading.enums.TrackingMethod
import dev.wayron.book_tracker_api.modules.repositories.reading.ReadingLogRepository
import dev.wayron.book_tracker_api.modules.repositories.reading.ReadingSessionRepository
import dev.wayron.book_tracker_api.modules.services.book.BookService
import dev.wayron.book_tracker_api.modules.validations.Validator
import dev.wayron.book_tracker_api.utils.Mappers
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class ReadingService(
  private val sessionRepository: ReadingSessionRepository,
  private val logRepository: ReadingLogRepository,
  private val bookService: BookService,
  private val logValidator: Validator<ReadingLog>,
  private val readingValidator: Validator<ReadingSession>
) {
  private val logger = LoggerFactory.getLogger(ReadingService::class.java)

  fun createReadingSession(readingSessionRequest: ReadingSessionRequest): ReadingSessionResponse {
    logger.info("Creating a ReadingSession for the book with ID: ${readingSessionRequest.bookId}")
    val book = bookService.getBookById(readingSessionRequest.bookId!!)

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
      startReadingDate = readingSessionRequest.startReadingDate?.truncatedTo(ChronoUnit.MINUTES) ?: LocalDateTime.now()
        .truncatedTo(ChronoUnit.MINUTES),
      endReadingDate = null,
      estimatedCompletionDate = readingSessionRequest.estimatedCompletionDate?.truncatedTo(ChronoUnit.MINUTES),
    )

    readingValidator.validate(newReadingSession)

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
    return session
  }

  fun getReadingSessionsByBookId(bookId: Int): List<ReadingSessionResponse> {
    logger.info("Fetching reading sessions for book ID: $bookId.")
    val book = bookService.getBookById(bookId)

    val list = sessionRepository.findByBookId(book.id).map { Mappers.mapReadingSessionToDTO(it) }

    logger.info("Found ${list.size} reading sessions for book ID: $bookId.")
    return list
  }

  fun addReading(readingSessionId: Int, quantityRead: Int): ReadingLogResponse {
    logger.info("Adding $quantityRead units to reading session ID: $readingSessionId")
    val session = getReadingSessionById(readingSessionId)

    if (session.readingState == ReadingState.READ) throw ReadingSessionCompletedException()
    val log = ReadingLog(
      id = 0,
      readingSession = session,
      dateOfReading = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES),
      quantityRead = quantityRead
    )

    logValidator.validate(log)

    session.addProgress(quantityRead)
    logger.info("$quantityRead units added to session ID: $readingSessionId")

    logRepository.save(log)
    logger.info("Reading log saved (session ID: $readingSessionId, quantity: $quantityRead).")

    sessionRepository.save(session)
    logger.info("Updated reading session saved (ID: $readingSessionId).")
    return Mappers.mapReadingLogToDTO(log)
  }

}