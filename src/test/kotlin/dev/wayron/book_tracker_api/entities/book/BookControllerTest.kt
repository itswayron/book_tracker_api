package dev.wayron.book_tracker_api.entities.book

import com.fasterxml.jackson.databind.ObjectMapper
import dev.wayron.book_tracker_api.entities.book.model.Book
import dev.wayron.book_tracker_api.exceptions.book.BookNotFoundException
import dev.wayron.book_tracker_api.exceptions.book.BookNotValidException
import dev.wayron.book_tracker_api.validations.ValidationErrorMessages
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(BookController::class)
class BookControllerTest {

  @Autowired
  private lateinit var mockMvc: MockMvc

  @Autowired
  private lateinit var objectMapper: ObjectMapper

  @MockitoBean
  private lateinit var service: BookService
  private lateinit var book: Book

  @BeforeEach
  fun setUp() {
    book = Book(
      id = 1,
      title = "Example book",
      author = "Example author",
      pages = 100,
      chapters = 10,
      synopsis = null,
      publisher = null,
      publicationDate = null,
      language = null,
      isbn10 = null,
      isbn13 = null,
      typeOfMedia = null,
      genres = null
    )
  }

  @Test
  fun `should create book successfully`() {
    `when`(service.createBook(book)).thenReturn(book)

    val bookJson = objectMapper.writeValueAsString(book)

    mockMvc.perform(
      post("/books")
        .contentType(MediaType.APPLICATION_JSON)
        .content(bookJson)
    ).andExpect(status().isCreated)
      .andExpect(jsonPath("$.id").value(book.id))
      .andExpect(jsonPath("$.title").value(book.title))
      .andExpect(jsonPath("$.author").value(book.author))
      .andExpect(jsonPath("$.pages").value(book.pages))
      .andExpect(jsonPath("$.chapters").value(book.chapters))

    verify(service, times(1)).createBook(book)
  }

  @Test
  fun `should return bad request when creating book invalid`() {
    val invalidBook = book.copy(title = "", author = "", pages = -1, chapters = -1)
    `when`(service.createBook(invalidBook)).thenThrow(
      BookNotValidException(
        listOf(
          ValidationErrorMessages.EMPTY_TITLE.message,
          ValidationErrorMessages.EMPTY_AUTHOR.message,
          ValidationErrorMessages.PAGES_NOT_POSITIVE.message,
          ValidationErrorMessages.NEGATIVE_CHAPTERS.message
        )
      )
    )

    val invalidBookJson = objectMapper.writeValueAsString(invalidBook)

    mockMvc.perform(
      post("/books")
        .contentType(MediaType.APPLICATION_JSON)
        .content(invalidBookJson)
    ).andExpect(status().isBadRequest)
      .andExpect(jsonPath("$.status").value(400))
      .andExpect(jsonPath("$.error").value("Bad Request"))
      .andExpect(jsonPath("$.message").value("Book is not valid."))
      .andExpect(jsonPath("$.path").value("/books"))
      .andExpect(jsonPath("$.details").isArray)
      .andExpect(jsonPath("$.details[0]").value(ValidationErrorMessages.EMPTY_TITLE.message))
      .andExpect(jsonPath("$.details[1]").value(ValidationErrorMessages.EMPTY_AUTHOR.message))
      .andExpect(jsonPath("$.details[2]").value(ValidationErrorMessages.PAGES_NOT_POSITIVE.message))
      .andExpect(jsonPath("$.details[3]").value(ValidationErrorMessages.NEGATIVE_CHAPTERS.message))
  }

  @Test
  fun `should return list of books`() {
    val list = listOf(book, book.copy(title = "Another book", author = "Another author"))
    `when`(service.getBooks()).thenReturn(list)

    mockMvc.perform(get("/books"))
      .andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.size()").value(list.size))
      .andExpect(jsonPath("$[0].title").value("Example book"))
      .andExpect(jsonPath("$[0].author").value("Example author"))
      .andExpect(jsonPath("$[1].title").value("Another book"))
      .andExpect(jsonPath("$[1].author").value("Another author"))

    verify(service, times(1)).getBooks()
  }

  @Test
  fun `should return empty list when no books are found`() {
    `when`(service.getBooks()).thenReturn(emptyList())

    mockMvc.perform(get("/books"))
      .andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.size()").value(0))

    verify(service, times(1)).getBooks()
  }

  @Test
  fun `should return single book`() {
    `when`(service.getBookById(book.id)).thenReturn(book)

    mockMvc.perform(get("/books/${book.id}"))
      .andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(book.id))
      .andExpect(jsonPath("$.title").value(book.title))
      .andExpect(jsonPath("$.author").value(book.author))
      .andExpect(jsonPath("$.pages").value(book.pages))
      .andExpect(jsonPath("$.chapters").value(book.chapters))

    verify(service, times(1)).getBookById(1)
  }

  @Test
  fun `should return 404 when book is not found`() {
    `when`(service.getBookById(99)).thenThrow(BookNotFoundException())

    mockMvc.perform(get("/books/99"))
      .andExpect(status().isNotFound)

    verify(service, times(1)).getBookById(99)
  }

  @Test
  fun `should return bad request when updating invalid book`() {
    val invalidBook = book.copy(title = "")
    `when`(service.createBook(book)).thenReturn(book)
    `when`(service.updateBook(Pair(1, invalidBook))).thenThrow(BookNotValidException(listOf("Invalid book")))

    val invalidBookJson = objectMapper.writeValueAsString(invalidBook)

    mockMvc.perform(
      put("/books/${book.id}")
        .contentType(MediaType.APPLICATION_JSON)
        .content(invalidBookJson)
    ).andExpect(status().isBadRequest)

  }

  @Test
  fun `should update book successfully`() {
    val newBook = book.copy(title = "Book updated", author = "Author updated.")
    `when`(service.createBook(book)).thenReturn(book)
    `when`(service.updateBook(Pair(1, newBook))).thenReturn(newBook)

    val newBookJson = objectMapper.writeValueAsString(newBook)

    mockMvc.perform(
      put("/books/${book.id}")
        .contentType(MediaType.APPLICATION_JSON)
        .content(newBookJson)
    ).andExpect(status().isOk)
      .andExpect(jsonPath("$.id").value(newBook.id))
      .andExpect(jsonPath("$.title").value(newBook.title))
      .andExpect(jsonPath("$.author").value(newBook.author))
      .andExpect(jsonPath("$.pages").value(newBook.pages))
      .andExpect(jsonPath("$.chapters").value(newBook.chapters))

    verify(service, times(1)).updateBook(Pair(1, newBook))
  }

  @Test
  fun `should return 404 when book is not found while updating the book`() {
    val newBook = book.copy(title = "Book updated", author = "Author updated.")
    `when`(service.createBook(book)).thenReturn(book)
    `when`(service.updateBook(Pair(99, newBook))).thenThrow(BookNotFoundException())

    val newBookJson = objectMapper.writeValueAsString(newBook)

    mockMvc.perform(
      put("/books/99")
        .contentType(MediaType.APPLICATION_JSON)
        .content(newBookJson)
    ).andExpect(status().isNotFound)

    verify(service, times(1)).updateBook(Pair(99, newBook))
  }

  @Test
  fun `should delete book successfully when book exist`() {
    doNothing().`when`(service).deleteBook(1)

    mockMvc.perform(delete("/books/1"))
      .andExpect(status().isNoContent)

    verify(service, times(1)).deleteBook(1)
  }

  @Test
  fun `should return 404 when book to delete is not found`() {
    `when`(service.deleteBook(99)).thenThrow(BookNotFoundException())

    mockMvc.perform(delete("/books/99"))
      .andExpect(status().isNotFound)

    verify(service, times(1)).deleteBook(99)
  }
}