package dev.wayron.book_tracker_api.modules.services.book

import dev.wayron.book_tracker_api.modules.models.book.Book
import dev.wayron.book_tracker_api.modules.repositories.book.BookRepository
import dev.wayron.book_tracker_api.modules.exceptions.ExceptionErrorMessages
import dev.wayron.book_tracker_api.modules.exceptions.book.BookNotFoundException
import dev.wayron.book_tracker_api.modules.exceptions.book.BookNotValidException
import dev.wayron.book_tracker_api.utils.ValidationErrorMessages
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
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
  fun `should successfully create a new book`() {
    `when`(repository.save(any<Book>())).thenReturn(book)

    val result = service.createBook(book)
    assert(result.title == "Example book")
    assert(result.author == "Example author")
    assert(result.id == 1)

    verify(repository, times(1)).save(any<Book>())
  }

  @Test
  fun `should throw exception for invalid book creation`() {
    val invalidBook = book.copy(title = "")

    val exception = assertThrows<BookNotValidException> { service.createBook(invalidBook) }

    assertEquals(ExceptionErrorMessages.BOOK_NOT_VALID.message, exception.message)
    verify(repository, never()).save(any<Book>())
  }

  @Test
  fun `should return a list of books successfully`() {
    val books = listOf(book)
    val pageableResponse = PageImpl(books)
    `when`(repository.findAll(any<Pageable>())).thenReturn(pageableResponse)

    val pageable = PageRequest.of(0, 10)
    val result = service.getBooks(pageable)

    assert(result.content.size == 1)
    assert(result.content[0].title == "Example book")
    verify(repository, times(1)).findAll(any<Pageable>())
  }

  @Test
  fun `should return the correct book by id`() {
    `when`(repository.findById(book.id)).thenReturn(Optional.of(book))

    val result = service.getBookById(1)

    assertEquals(result.title, "Example book")
    assertEquals(result.author, "Example author")
    assertEquals(result.pages, 100)
    assertEquals(result.chapters, 10)
    verify(repository, times(1)).findById(1)
  }

  @Test
  fun `should throw exception if book is not found by id`() {
    `when`(repository.findById(2)).thenReturn(Optional.empty())

    val exception = assertThrows<BookNotFoundException> { service.getBookById(2) }

    assert(exception.message == ExceptionErrorMessages.BOOK_NOT_FOUND.message)
    verify(repository, times(1)).findById(2)
  }

  @Test
  fun `should successfully update a book`() {
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
  fun `should throw exception for invalid book update`() {
    val invalidBook = book.copy(title = "")
    val command = Pair(1, invalidBook)

    val exception = assertThrows<BookNotValidException> { service.updateBook(command) }

    assertEquals(ExceptionErrorMessages.BOOK_NOT_VALID.message, exception.message)
    assert(exception.errors.contains(ValidationErrorMessages.EMPTY_TITLE.message))

    verify(repository, never()).save(any<Book>())
  }

  @Test
  fun `should successfully delete a book if it exists`() {
    `when`(repository.findById(book.id)).thenReturn(Optional.of(book))
    doNothing().`when`(repository).deleteById(book.id)

    service.deleteBook(book.id)

    verify(repository, times(1)).deleteById(book.id)
  }

  @Test
  fun `should throw exception if book to delete is not found`() {
    val bookId = 99
    `when`(repository.findById(bookId)).thenThrow(BookNotFoundException())

    val exception = assertThrows<BookNotFoundException> { service.deleteBook(bookId) }

    assertEquals(ExceptionErrorMessages.BOOK_NOT_FOUND.message, exception.message)
    verify(repository, never()).deleteById(anyInt())
  }
}