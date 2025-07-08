package dev.wayron.book_tracker_api.modules.services.book

import dev.wayron.book_tracker_api.modules.exceptions.ExceptionErrorMessages
import dev.wayron.book_tracker_api.modules.exceptions.ExceptionProvider
import dev.wayron.book_tracker_api.modules.exceptions.book.BookNotFoundException
import dev.wayron.book_tracker_api.modules.exceptions.book.BookNotValidException
import dev.wayron.book_tracker_api.modules.models.book.Book
import dev.wayron.book_tracker_api.modules.models.book.BookPatch
import dev.wayron.book_tracker_api.modules.models.book.BookRequest
import dev.wayron.book_tracker_api.modules.models.book.BookResponse
import dev.wayron.book_tracker_api.modules.models.user.User
import dev.wayron.book_tracker_api.modules.repositories.book.BookRepository
import dev.wayron.book_tracker_api.modules.repositories.user.UserRepository
import dev.wayron.book_tracker_api.modules.services.ImageService
import dev.wayron.book_tracker_api.modules.validators.ValidationErrorMessages
import dev.wayron.book_tracker_api.modules.validators.Validator
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.multipart.MultipartFile
import java.sql.Timestamp
import java.util.*
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class BookServiceTest {

  @Mock
  lateinit var repository: BookRepository

  @Mock
  lateinit var validator: Validator<Book>

  @Mock
  lateinit var userRepository: UserRepository

  @Mock
  lateinit var imageService: ImageService

  @InjectMocks
  lateinit var service: BookService

  private lateinit var book: Book
  private lateinit var user: User
  private lateinit var bookRequest: BookRequest
  private lateinit var bookResponse: BookResponse
  private lateinit var bookPatch: BookPatch

  fun <T> any(): T = Mockito.any<T>() ?: uninitialized()

  @Suppress("UNCHECKED_CAST")
  fun <T> uninitialized(): T = null as T

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
      synopsis = "A synopsis describing the book.",
      publisher = "A publisher to the book.",
      publicationDate = null,
      language = null,
      isbn10 = null,
      isbn13 = null,
      typeOfMedia = null,
      genres = null,
      createdAt = Timestamp(System.currentTimeMillis()),
      updatedAt = Timestamp(System.currentTimeMillis()),
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
    bookPatch = BookPatch(
      title = book.title,
      author = book.author,
      pages = book.pages,
      chapters = book.chapters,
    )
  }

  @BeforeEach
  fun setupSecurityContext() {
    val authentication = UsernamePasswordAuthenticationToken("Example user", null, emptyList())
    SecurityContextHolder.getContext().authentication = authentication
  }

  @Test
  fun `should successfully create a new book`() {
    `when`(userRepository.findByUsernameField("Example user")).thenReturn(user)
    `when`(repository.save(any<Book>())).thenReturn(book)

    val result = service.createBook(bookRequest)
    assert(result.title == "Example book")
    assert(result.author == "Example author")
    assert(result.id == 1)

    verify(repository, times(1)).save(any<Book>())
  }

  @Test
  fun `should throw exception for invalid book creation`() {
    book.copy(title = "")
    val invalidBookRequest = bookRequest.copy(title = "")

    `when`(userRepository.findByUsernameField("Example user")).thenReturn(user)

    doThrow(BookNotValidException(listOf(ValidationErrorMessages.EMPTY_TITLE.message)))
      .`when`(validator).validate(any())

    val exception = assertThrows<BookNotValidException> {
      service.createBook(invalidBookRequest)
    }

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

    assert(exception.apiMessage == ExceptionErrorMessages.BOOK_NOT_FOUND.message)
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
    val bookUpdatedRequest = bookPatch.copy(
      title = "New Title",
      author = "New Author",
      pages = 200,
      chapters = 10,
    )
    val command = Pair(1, bookUpdatedRequest)

    `when`(repository.findById(1)).thenReturn(Optional.of(book))
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
    val invalidBookRequest = bookPatch.copy(title = "")
    val bookFound = book.copy(title = "Valid title")

    given(repository.findById(book.id)).willReturn(Optional.of(bookFound))

    doThrow(BookNotValidException(listOf(ValidationErrorMessages.EMPTY_TITLE.message)))
      .`when`(validator).validate(any())

    val command = Pair(1, invalidBookRequest)

    val exception = assertThrows<BookNotValidException> {
      service.updateBook(command)
    }

    assertEquals(ExceptionErrorMessages.BOOK_NOT_VALID.message, exception.message)
    assert(exception.errors.contains(ValidationErrorMessages.EMPTY_TITLE.message))

    verify(repository, never()).save(any())
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

    assertEquals(ExceptionErrorMessages.BOOK_NOT_FOUND.message, exception.apiMessage)
    verify(repository, never()).deleteById(anyInt())
  }

  @Test
  fun `should upload cover and delete old image when coverPath is not null`() {
    val bookWithOldCover = book.copy(coverPath = "old/path/to/cover.jpg")

    `when`(repository.findById(book.id)).thenReturn(Optional.of(bookWithOldCover))

    val newImagePath = "new/path/to/cover.jpg"
    `when`(imageService.saveImage(any(), any(), any())).thenReturn(newImagePath)

    val coverFile = mock(MultipartFile::class.java)
    service.uploadCover(book.id, coverFile)

    verify(imageService, times(1)).deleteImage("old/path/to/cover.jpg")

    val captor = ArgumentCaptor.forClass(Book::class.java)
    verify(repository).save(captor.capture())

    val savedBook = captor.value
    assertEquals(newImagePath, savedBook.coverPath)
  }

  @Test
  fun `should upload cover and not delete old image when coverPath is null`() {
    val bookWithoutCover = book.copy(coverPath = null)

    `when`(repository.findById(book.id)).thenReturn(Optional.of(bookWithoutCover))

    val newImagePath = "new/path/to/cover.jpg"
    `when`(imageService.saveImage(any(), any(), any())).thenReturn(newImagePath)

    val coverFile = mock(MultipartFile::class.java)
    service.uploadCover(book.id, coverFile)

    verify(imageService, never()).deleteImage(anyString())

    verify(imageService, times(1)).saveImage(any(), any(), any())

    val captor = ArgumentCaptor.forClass(Book::class.java)
    verify(repository).save(captor.capture())

    val savedBook = captor.value
    assertEquals(newImagePath, savedBook.coverPath)
    assert(savedBook.updatedAt.time >= savedBook.createdAt.time)
  }

  @Test
  fun `should upload cover and continue if deleting old image throws exception`() {
    val bookWithOldCover = book.copy(coverPath = "old/path/to/cover.jpg")
    `when`(repository.findById(book.id)).thenReturn(Optional.of(bookWithOldCover))

    doThrow(RuntimeException("Failed to delete image")).`when`(imageService).deleteImage("old/path/to/cover.jpg")

    val newImagePath = "new/path/to/cover.jpg"
    `when`(imageService.saveImage(any(), any(), any())).thenReturn(newImagePath)

    val coverFile = mock(MultipartFile::class.java)

    service.uploadCover(book.id, coverFile)

    verify(imageService, times(1)).deleteImage("old/path/to/cover.jpg")

    verify(imageService, times(1)).saveImage(any(), any(), any())

    val captor = ArgumentCaptor.forClass(Book::class.java)
    verify(repository).save(captor.capture())
    assertEquals(newImagePath, captor.value.coverPath)
  }

  @Test
  fun `should throw BookNotFoundException when book not found during cover upload`() {
    val invalidBookId = 999

    val repository = mock(BookRepository::class.java, withSettings().extraInterfaces(ExceptionProvider::class.java))

    `when`(repository.findById(invalidBookId)).thenReturn(Optional.empty())

    `when`((repository as ExceptionProvider<Int>).notFoundException(invalidBookId))
      .thenReturn(BookNotFoundException(invalidBookId))

    @Suppress("UNCHECKED_CAST")
    val validator = mock(Validator::class.java) as Validator<Book>
    val userRepository = mock(UserRepository::class.java)
    val imageService = mock(ImageService::class.java)

    val service = BookService(repository, validator, userRepository, imageService)

    val coverFile = mock(MultipartFile::class.java)

    val exception = assertThrows(BookNotFoundException::class.java) {
      service.uploadCover(invalidBookId, coverFile)
    }

    assertEquals("Book ID=$invalidBookId not found.", exception.apiMessage)
    assertEquals(HttpStatus.NOT_FOUND, exception.status)
  }
}
