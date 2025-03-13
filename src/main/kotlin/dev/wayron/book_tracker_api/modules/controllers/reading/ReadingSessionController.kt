package dev.wayron.book_tracker_api.modules.controllers.reading

import dev.wayron.book_tracker_api.modules.models.reading.dto.AddReadingRequest
import dev.wayron.book_tracker_api.modules.models.reading.dto.ReadingSessionRequest
import dev.wayron.book_tracker_api.modules.models.reading.dto.ReadingLogResponse
import dev.wayron.book_tracker_api.modules.models.reading.dto.ReadingSessionResponse
import dev.wayron.book_tracker_api.modules.services.reading.ReadingService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/readings")
class ReadingSessionController(private val service: ReadingService) {

  @PostMapping("/{bookId}")
  fun createSessionReading(
    @PathVariable bookId: Int,
    @RequestBody readingSessionRequest: ReadingSessionRequest?
  ): ResponseEntity<ReadingSessionResponse> {
    val request: ReadingSessionRequest = readingSessionRequest ?: ReadingSessionRequest()
    request.bookId = bookId
    return ResponseEntity.status(HttpStatus.CREATED).body(service.createReadingSession(request))
  }

  @GetMapping("/{bookId}")
  fun getBookReadingSessions(@PathVariable bookId: Int): ResponseEntity<List<ReadingSessionResponse>> {
    return ResponseEntity.status(HttpStatus.OK).body(service.getReadingSessionsByBookId(bookId))
  }

  @PostMapping("/add/{sessionId}")
  fun addReading(@PathVariable sessionId: Int, @RequestBody @Valid request: AddReadingRequest): ResponseEntity<ReadingLogResponse> {
    return ResponseEntity.status(HttpStatus.OK).body(service.addReading(sessionId, request.quantityRead))
  }

}