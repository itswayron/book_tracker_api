package dev.wayron.book_tracker_api.modules.validators.reading

import dev.wayron.book_tracker_api.modules.exceptions.reading.ReadingSessionNotValidException
import dev.wayron.book_tracker_api.modules.models.book.Book
import dev.wayron.book_tracker_api.modules.models.reading.ReadingSession
import dev.wayron.book_tracker_api.modules.models.reading.enums.TrackingMethod
import dev.wayron.book_tracker_api.modules.validators.ValidationErrorMessages
import dev.wayron.book_tracker_api.modules.models.user.User
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertTrue


class ReadingSessionValidatorTest {

  private lateinit var validator: ReadingSessionValidator
  private lateinit var session: ReadingSession
  private lateinit var user: User

  @BeforeEach
  fun setUp() {
    user = User(
      usernameField = "Example user",
      email = "Example email",
      passwordField = "A very secure password"
    )
    validator = ReadingSessionValidator()
    val book = Book(
      id = 1,
      title = "Example title.",
      author = "Example author.",
      pages = 100,
      chapters = 10,
      userId = user
    )
    session = ReadingSession(
      id = 1,
      book = book,
      progressInPercentage = 10.0,
      totalProgress = 10,
      pages = book.pages,
      chapters = book.chapters,
      trackingMethod = TrackingMethod.PAGES,
      userId = user
    )
  }

  @Test
  fun `should throw exception when pages is not positive`() {
    val invalidSession = session.copy(pages = 0)

    val exception = assertThrows<ReadingSessionNotValidException> { validator.validate(invalidSession) }

    assertTrue(exception.errors.contains(ValidationErrorMessages.PAGES_NOT_POSITIVE.message))
  }

  @Test
  fun `should throw exception when tracking by chapters and book does not have chapters`() {
    val invalidSession = session.copy(trackingMethod = TrackingMethod.CHAPTERS, chapters = 0)

    val exception = assertThrows<ReadingSessionNotValidException> { validator.validate(invalidSession) }

    assertTrue(exception.errors.contains(ValidationErrorMessages.BOOK_HAS_NO_CHAPTERS.message))
  }

  @Test
  fun `should throw exception when daily goal is negative`() {
    val invalidSession = session.copy(dailyGoal = -10)

    val exception = assertThrows<ReadingSessionNotValidException> { validator.validate(invalidSession) }

    assertTrue(exception.errors.contains(ValidationErrorMessages.NEGATIVE_DAILY_GOAL.message))
  }

  @Test
  fun `should throw exception when start reading is in the future`(){
    val invalidSession = session.copy(startReadingDate = LocalDateTime.now().plusDays(1))

    val exception = assertThrows<ReadingSessionNotValidException> { validator.validate(invalidSession) }

    assertTrue(exception.errors.contains(ValidationErrorMessages.FUTURE_START_READING.message))

  }

  @Test
  fun `should throw exception when end reading is in the future`(){
    val invalidSession = session.copy(endReadingDate = LocalDateTime.now().plusDays(1))

    val exception = assertThrows<ReadingSessionNotValidException> { validator.validate(invalidSession) }

    assertTrue(exception.errors.contains(ValidationErrorMessages.FUTURE_END_READING.message))

  }

  @Test
  fun `should throw exception when start date is after estimated completion date`() {
    val invalidSession = session.copy(
      startReadingDate = LocalDateTime.of(2024, 1, 10, 0, 0),
      estimatedCompletionDate = LocalDateTime.of(2024, 1, 5, 0, 0)
    )

    val exception = assertThrows<ReadingSessionNotValidException> { validator.validate(invalidSession) }

    assertTrue(exception.errors.contains(ValidationErrorMessages.INVALID_ESTIMATED_COMPLETION_DATE.message))
  }
}
