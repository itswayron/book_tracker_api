package dev.wayron.book_tracker_api.modules.controllers.book

import dev.wayron.book_tracker_api.config.ApiRoutes
import dev.wayron.book_tracker_api.modules.models.book.BookPatch
import dev.wayron.book_tracker_api.modules.models.book.BookRequest
import dev.wayron.book_tracker_api.modules.models.book.BookResponse
import dev.wayron.book_tracker_api.modules.services.book.BookService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping(ApiRoutes.BOOKS)
@SecurityRequirement(name = "bearerAuth")
class BookController(private val service: BookService) {

  @PostMapping
  fun createBook(
    @RequestBody @Valid book: BookRequest,
    //@RequestPart("cover", required = false) coverFile: MultipartFile?
  ): ResponseEntity<BookResponse> {
    val response = service.createBook(book)
    return ResponseEntity(response, HttpStatus.CREATED)
  }

  @GetMapping
  fun getBooks(
    @RequestParam(defaultValue = "0") page: Int,
    @RequestParam(defaultValue = "10") size: Int,
    @RequestParam(defaultValue = "updatedAt") sort: String,
    @RequestParam(defaultValue = "DESC") direction: String
  ): ResponseEntity<Page<BookResponse>> {
    val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort))
    val response = service.getBooks(pageable)
    return ResponseEntity(response, HttpStatus.OK)
  }

  @GetMapping("/{id}")
  fun getBookById(@PathVariable id: Int): ResponseEntity<BookResponse> {
    val response = service.getBookById(id)
    return ResponseEntity(response, HttpStatus.OK)
  }

  @PatchMapping("/{id}")
  fun updateBook(@PathVariable id: Int, @RequestBody book: BookPatch): ResponseEntity<BookResponse> {
    val response = service.updateBook(Pair(id, book))
    return ResponseEntity(response, HttpStatus.OK)
  }

  @DeleteMapping("/{id}")
  fun deleteBook(@PathVariable id: Int): ResponseEntity<Unit> {
    service.deleteBook(id)
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
  }
}
