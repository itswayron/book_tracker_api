package dev.wayron.book_tracker_api.book

import dev.wayron.book_tracker_api.book.model.Book
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class BookController(private val service: BookService) {

  @PostMapping("/books")
  fun createBook(@RequestBody book: Book): ResponseEntity<Book> {
    val bookCreated = service.createBook(book)
    return ResponseEntity.status(HttpStatus.CREATED).body(bookCreated)
  }

  @GetMapping("/books")
  fun getBooks(): ResponseEntity<List<Book>> {
    return ResponseEntity.status(HttpStatus.OK).body(service.getBooks())
  }

  @GetMapping("/books/{id}")
  fun getBookById(@PathVariable id: Int): ResponseEntity<Book> {
    return ResponseEntity.status(HttpStatus.OK).body(service.getBookById(id))
  }

  @PutMapping("/books/{id}")
  fun updateBook(@PathVariable id: Int, @RequestBody book: Book): ResponseEntity<Book> {
    return ResponseEntity.status(HttpStatus.OK).body(service.updateBook(Pair(id, book)))
  }

  @DeleteMapping("/books/{id}")
  fun deleteBook(@PathVariable id: Int): ResponseEntity<Void> {
    service.deleteBook(id)
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
  }

}