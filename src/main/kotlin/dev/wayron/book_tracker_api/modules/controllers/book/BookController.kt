package dev.wayron.book_tracker_api.modules.controllers.book

import dev.wayron.book_tracker_api.modules.models.book.BookRequest
import dev.wayron.book_tracker_api.modules.models.book.BookResponse
import dev.wayron.book_tracker_api.modules.models.mappers.BookMapper
import dev.wayron.book_tracker_api.modules.services.book.BookService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/books")
class BookController(private val service: BookService, private val mapper: BookMapper) {

  @PostMapping
  fun createBook(@RequestBody @Valid book: BookRequest): ResponseEntity<BookResponse> {
    val bookCreated = service.createBook(book)
    val response = ResponseEntity.status(HttpStatus.CREATED).body(bookCreated)
    return response
  }

  @GetMapping
  fun getBooks(
    @RequestParam(defaultValue = "0") page: Int,
    @RequestParam(defaultValue = "10") size: Int,
    @RequestParam(defaultValue = "updatedAt") sort: String,
    @RequestParam(defaultValue = "DESC") direction: String
  ): ResponseEntity<Page<BookResponse>> {
    val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort))
    val bookPage = service.getBooks(pageable)
    return ResponseEntity.status(HttpStatus.OK).body(bookPage)
  }

  @GetMapping("/{id}")
  fun getBookById(@PathVariable id: Int): ResponseEntity<BookResponse> {
    return ResponseEntity.status(HttpStatus.OK).body(mapper.entityBookToResponse(service.getBookById(id)))
  }

  @PutMapping("/{id}")
  fun updateBook(@PathVariable id: Int, @RequestBody book: BookRequest): ResponseEntity<BookResponse> {
    return ResponseEntity.status(HttpStatus.OK).body(service.updateBook(Pair(id, book)))
  }

  @DeleteMapping("/{id}")
  fun deleteBook(@PathVariable id: Int): ResponseEntity<Unit> {
    service.deleteBook(id)
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
  }

}