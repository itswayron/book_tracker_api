package dev.wayron.book_tracker_api.modules.controllers.book

import dev.wayron.book_tracker_api.modules.services.book.BookService
import dev.wayron.book_tracker_api.modules.models.book.Book
import dev.wayron.book_tracker_api.modules.models.book.BookDTO
import dev.wayron.book_tracker_api.utils.Mappers
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/books")
class BookController(private val service: BookService) {

  @PostMapping
  fun createBook(@RequestBody @Valid book: Book): ResponseEntity<BookDTO> {
    val bookCreated = service.createBook(book)
    return ResponseEntity.status(HttpStatus.CREATED).body(Mappers.mapBookToDTO(bookCreated))
  }

  @GetMapping
  fun getBooks(
    @RequestParam(defaultValue = "0") page: Int,
    @RequestParam(defaultValue = "10") size: Int,
    @RequestParam(defaultValue = "updatedAt") sort: String,
    @RequestParam(defaultValue = "DESC") direction: String
  ): ResponseEntity<Page<BookDTO>> {
    val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort))
    val bookPage = service.getBooks(pageable).map { Mappers.mapBookToDTO(it) }
    return ResponseEntity.status(HttpStatus.OK).body(bookPage)
  }

  @GetMapping("/{id}")
  fun getBookById(@PathVariable id: Int): ResponseEntity<BookDTO> {
    return ResponseEntity.status(HttpStatus.OK).body(Mappers.mapBookToDTO(service.getBookById(id)))
  }

  @PutMapping("/{id}")
  fun updateBook(@PathVariable id: Int, @RequestBody book: Book): ResponseEntity<BookDTO> {
    return ResponseEntity.status(HttpStatus.OK).body(Mappers.mapBookToDTO(service.updateBook(Pair(id, book))))
  }

  @DeleteMapping("/{id}")
  fun deleteBook(@PathVariable id: Int): ResponseEntity<Void> {
    service.deleteBook(id)
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
  }

}