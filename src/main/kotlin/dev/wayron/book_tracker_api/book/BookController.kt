package dev.wayron.book_tracker_api.book

import dev.wayron.book_tracker_api.book.model.Book
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/books")
class BookController(private val service: BookService) {

  @PostMapping
  fun createBook(@RequestBody book: Book): ResponseEntity<Book> {
    val bookCreated = service.createBook(book)
    return ResponseEntity.status(HttpStatus.CREATED).body(bookCreated)
  }

  @GetMapping
  fun getBooks(): ResponseEntity<List<Book>> {
    return ResponseEntity.status(HttpStatus.OK).body(service.getBooks())
  }

  @GetMapping("/{id}")
  fun getBookById(@PathVariable id: Int): ResponseEntity<Book> {
    return ResponseEntity.status(HttpStatus.OK).body(service.getBookById(id))
  }

  @PutMapping("/{id}")
  fun updateBook(@PathVariable id: Int, @RequestBody book: Book): ResponseEntity<Book> {
    return ResponseEntity.status(HttpStatus.OK).body(service.updateBook(Pair(id, book)))
  }

  @DeleteMapping("/{id}")
  fun deleteBook(@PathVariable id: Int): ResponseEntity<Void> {
    service.deleteBook(id)
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
  }

}