package dev.wayron.book_tracker_api

import dev.wayron.book_tracker_api.entities.book.BookService
import dev.wayron.book_tracker_api.entities.book.model.Book
import dev.wayron.book_tracker_api.entities.book.repositories.BookRepository
import dev.wayron.book_tracker_api.exceptions.book.BookNotFoundException
import dev.wayron.book_tracker_api.exceptions.book.BookNotValidException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import java.sql.Timestamp
import java.util.*
import kotlin.test.assertEquals

class BookServiceTest {

  @Mock
  private val repository: BookRepository = mock(BookRepository::class.java)

  @InjectMocks
  private val service = BookService(repository)
  private lateinit var book: Book

  @BeforeEach
  fun setUp() {
    book = Book(
      id = 1,
      title = "Example book",
      author = "Example author",
      pages = 100,
      chapters = 10,
      synopsis = "A synopsis describing the book.",
      publisher = "A publisher to the book.",
      publicationDate = null,
      language = null,
      isbn10 = null,
      isbn13 = null,
      typeOfMedia = null,
      genres = null,
      createdAt = Timestamp(System.currentTimeMillis()),
      updatedAt = Timestamp(System.currentTimeMillis())
    )
  }

  @Test
  fun `createBook should create a new book successfully`() {
    `when`(repository.save(any<Book>())).thenReturn(book)

    val result = service.createBook(book)
    assert(result.title == "Example book")
    assert(result.author == "Example author")
    assert(result.id == 1)

    verify(repository, times(1)).save(any<Book>())
  }

  @Test
  fun `should throw exception when creating an invalid book`() {
    val invalidBook = book.copy(title = "")

    try {
      service.createBook(invalidBook)
    } catch (ex: BookNotValidException) {
      assert(ex.message == "Book is not valid.")
    }
    verify(repository, never()).save(any<Book>())
  }

  @Test
  fun `should return a list of books`() {
    val books = listOf(book)
    `when`(repository.findAll()).thenReturn(books)

    val result = service.getBooks()

    assert(result.size == 1)
    assert(result[0].title == "Example book")
    verify(repository, times(1)).findAll()
  }

  @Test
  fun `should return a single book`() {
    `when`(repository.findById(book.id)).thenReturn(Optional.of(book))

    val result = service.getBookById(1)

    assert(result.title == "Example book")
    assert(result.author == "Example author")
    assert(result.pages == 100)
    assert(result.chapters == 10)
    verify(repository, times(1)).findById(1)
  }

  @Test
  fun `should throw exception when searching invalid id`() {
    `when`(repository.findById(2)).thenReturn(Optional.empty())

    val exception = assertThrows<BookNotFoundException> { service.getBookById(2) }

    assert(exception.message == "Book not found.")
    verify(repository, times(1)).findById(2)
  }

  @Test
  fun `should update book successfully`() {
    val bookUpdated = book.copy(
      title = "New Title",
      author = "New Author",
      pages = 200,
      chapters = 10,
    )
    val command = Pair(1, bookUpdated)

    `when`(repository.save(any<Book>())).thenReturn(bookUpdated)
    val result = service.updateBook(command)

    assert(result.title == "New Title")
    assert(result.author == "New Author")
    assert(result.pages == 200)
    assert(result.chapters == 10)

    verify(repository, times(1)).save(any<Book>())
  }

  @Test
  fun `should throw exception when book is invalid`() {
    val invalidBook = book.copy(title = "")
    val command = Pair(1, invalidBook)

    try {
      service.updateBook(command)
    } catch (ex: BookNotValidException) {
      assert(ex.message == "Book is not valid.")
      assert(ex.errors.contains("Title cannot be empty."))
    }

    verify(repository, never()).save(any<Book>())
  }

  @Test
  fun `should delete book successfully when book exist`() {
    `when`(repository.findById(book.id)).thenReturn(Optional.of(book))
    `when`(repository.save(any<Book>())).thenReturn(book)

    service.createBook(book)
    service.deleteBook(book.id)

    verify(repository, times(1)).deleteById(book.id)
  }

  @Test
  fun `should throw exception when book does not exist`() {
    val bookId = 99
    `when`(repository.findById(bookId)).thenThrow(BookNotFoundException())

    val exception = assertThrows<BookNotFoundException> { service.deleteBook(bookId) }

    assertEquals("Book not found.", exception.message)
    verify(repository, never()).deleteById(anyInt())
  }
}