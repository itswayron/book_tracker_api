package dev.wayron.book_tracker_api.reading

import dev.wayron.book_tracker_api.reading.model.Reading
import dev.wayron.book_tracker_api.reading.model.ReadingRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/readings")
class ReadingController(private val service: ReadingService) {

  @PostMapping("/{bookId}")
  fun createReading(@PathVariable bookId: Int, @RequestBody readingRequest: ReadingRequest): ResponseEntity<Reading> {
    readingRequest.bookId = bookId
    return ResponseEntity.status(HttpStatus.CREATED).body(service.createReading(readingRequest))
  }


}