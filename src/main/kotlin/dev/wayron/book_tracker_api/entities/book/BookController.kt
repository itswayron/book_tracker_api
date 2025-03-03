package dev.wayron.book_tracker_api.entities.book

import dev.wayron.book_tracker_api.entities.book.model.Book
import dev.wayron.book_tracker_api.entities.book.model.BookDTO
import dev.wayron.book_tracker_api.utils.Mappers
import jakarta.validation.Valid
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
  fun getBooks(): ResponseEntity<List<BookDTO>> {
    return ResponseEntity.status(HttpStatus.OK).body(service.getBooks().map { Mappers.mapBookToDTO(it) })
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