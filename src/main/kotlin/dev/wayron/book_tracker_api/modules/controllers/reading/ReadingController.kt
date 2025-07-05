package dev.wayron.book_tracker_api.modules.controllers.reading

import dev.wayron.book_tracker_api.config.ApiRoutes
import dev.wayron.book_tracker_api.modules.models.reading.dto.AddReadingRequest
import dev.wayron.book_tracker_api.modules.models.reading.dto.ReadingLogResponse
import dev.wayron.book_tracker_api.modules.models.reading.dto.ReadingSessionRequest
import dev.wayron.book_tracker_api.modules.models.reading.dto.ReadingSessionResponse
import dev.wayron.book_tracker_api.modules.services.reading.ReadingService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ApiRoutes.READINGS)
@SecurityRequirement(name = "bearerAuth")
class ReadingController(private val service: ReadingService) {

  @PostMapping("/{bookId}")
  fun createSessionReading(
    @PathVariable bookId: Int,
    @RequestBody readingSessionRequest: ReadingSessionRequest?
  ): ResponseEntity<ReadingSessionResponse> {
    val request: ReadingSessionRequest = readingSessionRequest ?: ReadingSessionRequest()
    request.bookId = bookId
    val response = service.createReadingSession(request)
    return ResponseEntity(response, HttpStatus.CREATED)
  }

  @GetMapping("/{bookId}")
  fun getBookReadingSessions(@PathVariable bookId: Int): ResponseEntity<List<ReadingSessionResponse>> {
    val response = service.getReadingSessionsByBookId(bookId)
    return ResponseEntity(response, HttpStatus.OK)
  }

  @PostMapping("/add/{sessionId}")
  fun addReading(
    @PathVariable sessionId: Int,
    @RequestBody @Valid request: AddReadingRequest
  ): ResponseEntity<ReadingLogResponse> {
    val response = service.addReading(sessionId, request.quantityRead)
    return ResponseEntity(response, HttpStatus.OK)
  }

  @DeleteMapping("/delete/{sessionId}")
  fun deleteReadingById(@PathVariable sessionId: Int): ResponseEntity<Unit> {
    service.deleteReadingById(sessionId)
    return ResponseEntity.noContent().build()
  }
}
