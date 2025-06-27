package dev.wayron.book_tracker_api.modules.validations.book

import dev.wayron.book_tracker_api.modules.exceptions.book.BookNotValidException
import dev.wayron.book_tracker_api.modules.models.book.Book
import dev.wayron.book_tracker_api.modules.validations.ValidationErrorMessages
import dev.wayron.book_tracker_api.modules.models.user.UserEntity
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BookValidatorTest {

  private lateinit var validator: BookValidator
  private lateinit var book: Book
  private lateinit var user: UserEntity

  @BeforeEach
  fun setUp() {
    user = UserEntity(
      username = "Example user",
      email = "Example email",
      password = "A very secure password"
    )
    validator = BookValidator()
    book = Book(
      id = 1,
      title = "Example title",
      author = "Example author",
      pages = 300,
      chapters = 10,
      synopsis = null,
      publisher = null,
      publicationDate = null,
      language = null,
      isbn10 = null,
      isbn13 = null,
      typeOfMedia = null,
      genres = null,
      userId = user
    )
  }

  @Test
  fun `should throw exception when book is not valid`() {
    val invalidBook = book.copy(title = "", author = "", pages = -10, chapters = -1)

    val exception = assertThrows<BookNotValidException> { validator.validate(invalidBook) }

    assertEquals(4, exception.errors.size)
    assertTrue(exception.errors.contains(ValidationErrorMessages.EMPTY_TITLE.message))
    assertTrue(exception.errors.contains(ValidationErrorMessages.EMPTY_AUTHOR.message))
    assertTrue(exception.errors.contains(ValidationErrorMessages.PAGES_NOT_POSITIVE.message))
    assertTrue(exception.errors.contains(ValidationErrorMessages.NEGATIVE_CHAPTERS.message))
  }

  @Test
  fun `should not throw exception when book is valid`() {
    assertDoesNotThrow { validator.validate(book) }
  }

  @Test
  fun `should throw exception when chapters are negative`() {
    val invalidBook = book.copy(chapters = -5)
    val exception = assertThrows<BookNotValidException> { validator.validate(invalidBook) }
    assertTrue(exception.errors.contains(ValidationErrorMessages.NEGATIVE_CHAPTERS.message))
  }

  @Test
  fun `should not throw exception when chapters are null`() {
    val validBook = book.copy(chapters = null)
    assertDoesNotThrow { validator.validate(validBook) }
  }

  @Test
  fun `should throw exception when pages are negative`() {
    val invalidBook = book.copy(pages = -10)
    val exception = assertThrows<BookNotValidException> { validator.validate(invalidBook) }
    assertTrue(exception.errors.contains(ValidationErrorMessages.PAGES_NOT_POSITIVE.message))
  }

  @Test
  fun `should throw exception when pages are zero`() {
    val invalidBook = book.copy(pages = 0)
    val exception = assertThrows<BookNotValidException> { validator.validate(invalidBook) }
    assertTrue(exception.errors.contains(ValidationErrorMessages.PAGES_NOT_POSITIVE.message))
  }

  @Test
  fun `should throw exception when author is blank`() {
    val invalidBook = book.copy(author = "")
    val exception = assertThrows<BookNotValidException> { validator.validate(invalidBook) }
    assertTrue(exception.errors.contains(ValidationErrorMessages.EMPTY_AUTHOR.message))
  }

  @Test
  fun `should throw exception when title is blank`() {
    val invalidBook = book.copy(title = "")
    val exception = assertThrows<BookNotValidException> { validator.validate(invalidBook) }
    assertTrue(exception.errors.contains(ValidationErrorMessages.EMPTY_TITLE.message))
  }

}