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
class ReadingService(private val repository: ReadingRepository, private val webClient: WebClient) {
  private val logger = LoggerFactory.getLogger(ReadingService::class.java)

  fun createReading(readingRequest: ReadingRequest): Reading {
    val book = webClient.get()
      .uri("/books/${readingRequest.bookId}")
      .retrieve()
      .bodyToMono(Book::class.java)
      .block()

    val newReading = Reading(
      id = 0,
      bookId = book!!,
      progressInPercentage = 0.0,
      totalProgress = 0,
      pages = book.pages,
      chapters = book.chapters,
      readingState = ReadingState.READING,
      trackingMethod = readingRequest.trackingMethod ?: TrackingMethod.PAGES,
      dailyGoal = readingRequest.dailyGoal ?: 0,
      startReadingDate = readingRequest.startReadingDate ?: LocalDateTime.now(),
      endReadingDate = null,
      estimatedCompletionDate = readingRequest.estimatedCompletionDate,
    )
    logger.info("Creating reading $newReading")
    return repository.save(newReading)
  }

}