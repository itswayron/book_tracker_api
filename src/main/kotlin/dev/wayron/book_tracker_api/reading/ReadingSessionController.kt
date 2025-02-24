package dev.wayron.book_tracker_api.reading

import dev.wayron.book_tracker_api.reading.model.AddReadingRequest
import dev.wayron.book_tracker_api.reading.model.ReadingLog
import dev.wayron.book_tracker_api.reading.model.ReadingSession
import dev.wayron.book_tracker_api.reading.model.ReadingSessionRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/readings")
class ReadingSessionController(private val service: ReadingSessionService) {

  @PostMapping("/{bookId}")
  fun createSessionReading(
    @PathVariable bookId: Int,
    @RequestBody readingSessionRequest: ReadingSessionRequest?
  ): ResponseEntity<ReadingSession> {
    val request: ReadingSessionRequest = readingSessionRequest ?: ReadingSessionRequest()
    request.bookId = bookId
    return ResponseEntity.status(HttpStatus.CREATED).body(service.createReadingSession(request))
  }

  @GetMapping("/{bookId}")
  fun getBookReadingSessions(@PathVariable bookId: Int): ResponseEntity<List<ReadingSession>> {
    return ResponseEntity.status(HttpStatus.OK).body(service.getReadingSessionsByBookId(bookId))
  }

  @PostMapping("/add/{sessionId}")
  fun addReading(@PathVariable sessionId: Int, @RequestBody request: AddReadingRequest): ResponseEntity<ReadingLog> {
    return ResponseEntity.status(HttpStatus.OK).body(service.addReading(sessionId, request.quantityRead))
  }


}