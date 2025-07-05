package dev.wayron.book_tracker_api.modules.controllers.book

import com.fasterxml.jackson.databind.ObjectMapper
import dev.wayron.book_tracker_api.modules.exceptions.book.BookNotFoundException
import dev.wayron.book_tracker_api.modules.exceptions.book.BookNotValidException
import dev.wayron.book_tracker_api.modules.models.book.Book
import dev.wayron.book_tracker_api.modules.models.book.BookRequest
import dev.wayron.book_tracker_api.modules.models.book.BookResponse
import dev.wayron.book_tracker_api.modules.services.book.BookService
import dev.wayron.book_tracker_api.modules.validators.ValidationErrorMessages
import dev.wayron.book_tracker_api.modules.models.user.User
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
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
  private lateinit var bookRequest: BookRequest
  private lateinit var bookResponse: BookResponse
  private lateinit var user: User

  @BeforeEach
  fun setUp() {
    user = User(
      usernameField = "Example user",
      email = "Example email",
      passwordField = "A very secure password"
    )
    book = Book(
      id = 1,
      title = "Example book",
      author = "Example author",
      pages = 100,
      chapters = 10,
      userId = user
    )
    bookRequest = BookRequest(
      title = book.title,
      author = book.author,
      pages = book.pages,
      chapters = book.chapters,
    )
    bookResponse = BookResponse(
      id = 1,
      title = book.title,
      author = book.author,
      pages = book.pages,
      chapters = book.chapters,
    )
  }

  @Test
  fun `should successfully create a book`() {
    `when`(service.createBook(bookRequest)).thenReturn(bookResponse)

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

    verify(service, times(1)).createBook(bookRequest)
  }

  @Test
  fun `should return bad request for invalid book creation`() {
    val invalidBook = bookRequest.copy(title = "", author = "", pages = -1, chapters = -1)
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
  fun `should return a list of books successfully`() {
    val list = listOf(bookResponse, bookResponse.copy(title = "Another book", author = "Another author"))
    val pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "title"))
    val pageableResponse: Page<BookResponse> = PageImpl(list.sortedBy { it.title })
    `when`(service.getBooks(pageable)).thenReturn(pageableResponse)

    mockMvc.perform(get("/books?page=0&size=10&sort=title&direction=ASC"))
      .andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.content.size()").value(list.size))
      .andExpect(jsonPath("$.content[1].title").value("Example book"))
      .andExpect(jsonPath("$.content[1].author").value("Example author"))
      .andExpect(jsonPath("$.content[0].title").value("Another book"))
      .andExpect(jsonPath("$.content[0].author").value("Another author"))

    verify(service, times(1)).getBooks(pageable)
  }

  @Test
  fun `should return an empty list when no books are available`() {
    val pageableResponse = PageImpl(emptyList<BookResponse>())
    val pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "title"))
    `when`(service.getBooks(pageable)).thenReturn(pageableResponse)

    mockMvc.perform(get("/books?page=0&size=10&sort=title&direction=ASC"))
      .andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.content.size()").value(0))

    verify(service, times(1)).getBooks(pageable)
  }

  @Test
  fun `should return the correct book by id`() {
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
  fun `should return 404 if book is not found`() {
    `when`(service.getBookById(99)).thenThrow(BookNotFoundException())

    mockMvc.perform(get("/books/99"))
      .andExpect(status().isNotFound)

    verify(service, times(1)).getBookById(99)
  }

  @Test
  fun `should return bad request for invalid book update`() {
    val invalidBook = bookRequest.copy(title = "")
    `when`(service.createBook(bookRequest)).thenReturn(bookResponse)
    `when`(service.updateBook(Pair(1, invalidBook))).thenThrow(BookNotValidException(listOf("Invalid book")))

    val invalidBookJson = objectMapper.writeValueAsString(invalidBook)

    mockMvc.perform(
      put("/books/${book.id}")
        .contentType(MediaType.APPLICATION_JSON)
        .content(invalidBookJson)
    ).andExpect(status().isBadRequest)

  }

  @Test
  fun `should successfully update a book`() {
    val newBook = bookRequest.copy(title = "Book updated", author = "Author updated.")
    `when`(service.createBook(bookRequest)).thenReturn(bookResponse)
    `when`(service.updateBook(Pair(1, newBook))).thenReturn(bookResponse)

    val newBookJson = objectMapper.writeValueAsString(newBook)

    mockMvc.perform(
      put("/books/${book.id}")
        .contentType(MediaType.APPLICATION_JSON)
        .content(newBookJson)
    ).andExpect(status().isOk)
      .andExpect(jsonPath("$.title").value(newBook.title))
      .andExpect(jsonPath("$.author").value(newBook.author))
      .andExpect(jsonPath("$.pages").value(newBook.pages))
      .andExpect(jsonPath("$.chapters").value(newBook.chapters))

    verify(service, times(1)).updateBook(Pair(1, newBook))
  }

  @Test
  fun `should return 404 if book is not found during update`() {
    val newBook = bookRequest.copy(title = "Book updated", author = "Author updated.")
    `when`(service.createBook(bookRequest)).thenReturn(bookResponse)
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
  fun `should successfully delete a book if it exists`() {
    doNothing().`when`(service).deleteBook(1)

    mockMvc.perform(delete("/books/1"))
      .andExpect(status().isNoContent)

    verify(service, times(1)).deleteBook(1)
  }

  @Test
  fun `should return 404 if book to delete is not found`() {
    `when`(service.deleteBook(99)).thenThrow(BookNotFoundException())

    mockMvc.perform(delete("/books/99"))
      .andExpect(status().isNotFound)

    verify(service, times(1)).deleteBook(99)
  }
}