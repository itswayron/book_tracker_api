package dev.wayron.book_tracker_api.modules.validators.reading

import dev.wayron.book_tracker_api.modules.exceptions.reading.InvalidReadingLogException
import dev.wayron.book_tracker_api.modules.models.book.Book
import dev.wayron.book_tracker_api.modules.models.reading.ReadingLog
import dev.wayron.book_tracker_api.modules.models.reading.ReadingSession
import dev.wayron.book_tracker_api.modules.models.user.User
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class ReadingLogValidatorTest {

  private lateinit var validator: ReadingLogValidator
  private lateinit var log: ReadingLog
  private lateinit var user: User

  @BeforeEach
  fun setUp() {
    user = User(
      usernameField = "Example user",
      email = "Example email",
      passwordField = "A very secure password"
    )
    val book = Book(
      id = 1,
      title = "Example book.",
      author = "Example title",
      pages = 100,
      userId = user,
    )
    val session = ReadingSession(
      id = 1,
      book = book,
      progressInPercentage = 10.0,
      totalProgress = 10,
      pages = book.pages,
      userId = user,
    )
    validator = ReadingLogValidator()
    log = ReadingLog(
      id = 1,
      readingSession = session,
      dateOfReading = LocalDateTime.now(),
      quantityRead = 10,
    )
  }

  @Test
  fun `should throw exception when log is negative`() {
    val invalidLog = log.copy(quantityRead = -10)

    assertThrows<InvalidReadingLogException> { validator.validate(invalidLog) }
  }

  @Test
  fun `should throw exception when log is zero`() {
    val invalidLog = log.copy(quantityRead = 0)

    assertThrows<InvalidReadingLogException> { validator.validate(invalidLog) }
  }

  @Test
  fun `should not throw exception when log is valid`() {
    assertDoesNotThrow { validator.validate(log) }
  }
}
