package dev.wayron.book_tracker_api.modules.services.reading

import dev.wayron.book_tracker_api.modules.exceptions.ApiException
import dev.wayron.book_tracker_api.modules.exceptions.ExceptionErrorMessages
import dev.wayron.book_tracker_api.modules.exceptions.ExceptionProvider
import dev.wayron.book_tracker_api.modules.exceptions.book.BookNotFoundException
import dev.wayron.book_tracker_api.modules.exceptions.reading.InvalidReadingLogException
import dev.wayron.book_tracker_api.modules.exceptions.reading.ReadingSessionNotFoundException
import dev.wayron.book_tracker_api.modules.exceptions.reading.ReadingSessionNotValidException
import dev.wayron.book_tracker_api.modules.models.book.Book
import dev.wayron.book_tracker_api.modules.models.mappers.toResponse
import dev.wayron.book_tracker_api.modules.models.reading.ReadingLog
import dev.wayron.book_tracker_api.modules.models.reading.ReadingSession
import dev.wayron.book_tracker_api.modules.models.reading.dto.ReadingSessionRequest
import dev.wayron.book_tracker_api.modules.models.reading.enums.ReadingState
import dev.wayron.book_tracker_api.modules.models.reading.enums.TrackingMethod
import dev.wayron.book_tracker_api.modules.models.user.User
import dev.wayron.book_tracker_api.modules.repositories.book.BookRepository
import dev.wayron.book_tracker_api.modules.repositories.reading.ReadingLogRepository
import dev.wayron.book_tracker_api.modules.repositories.reading.ReadingSessionRepository
import dev.wayron.book_tracker_api.modules.repositories.user.UserRepository
import dev.wayron.book_tracker_api.modules.services.book.BookService
import dev.wayron.book_tracker_api.modules.validators.ValidationErrorMessages
import dev.wayron.book_tracker_api.modules.validators.Validator
import dev.wayron.book_tracker_api.modules.validators.reading.ReadingLogValidator
import dev.wayron.book_tracker_api.modules.validators.reading.ReadingSessionValidator
import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(MockitoExtension::class)
class ReadingServiceTest {
  @Mock
  lateinit var sessionRepository: ReadingSessionRepository

  @Mock
  lateinit var logRepository: ReadingLogRepository

  @Mock
  lateinit var bookRepository: BookRepository

  @Mock
  lateinit var bookService: BookService

  @Mock
  lateinit var userRepository: UserRepository

  lateinit var readingService: ReadingService

  private lateinit var book: Book
  private lateinit var reading: ReadingSession
  private lateinit var readingRequest: ReadingSessionRequest
  private lateinit var readingLog: ReadingLog
  private lateinit var user: User

  @BeforeEach
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    val logValidator: Validator<ReadingLog> = ReadingLogValidator()
    val readingValidator: Validator<ReadingSession> = ReadingSessionValidator()
    readingService = ReadingService(
      sessionRepository = sessionRepository,
      logRepository = logRepository,
      bookRepository = bookRepository,
      logValidator = logValidator,
      readingValidator = readingValidator,
      userRepository = userRepository,
    )
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
      chapters = 0,
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
    reading = ReadingSession(
      id = 1,
      book = book,
      progressInPercentage = 0.0,
      totalProgress = 0,
      pages = book.pages,
      chapters = book.chapters,
      readingState = ReadingState.READING,
      trackingMethod = TrackingMethod.PAGES,
      dailyGoal = 10,
      startReadingDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES),
      endReadingDate = null,
      estimatedCompletionDate = null,
      userId = user
    )
    readingRequest = ReadingSessionRequest(
      trackingMethod = reading.trackingMethod,
      dailyGoal = reading.dailyGoal,
      startReadingDate = reading.startReadingDate,
      estimatedCompletionDate = reading.estimatedCompletionDate,
    )
    readingLog = ReadingLog(
      id = 0,
      readingSession = reading,
      quantityRead = 10,
    )
    lenient().`when`(bookRepository.findById(1)).thenReturn(Optional.of(book))
    lenient().`when`(bookService.getBookById(book.id)).thenReturn(book.toResponse())
  }

  @BeforeEach
  fun setupSecurityContext() {
    lenient().`when`(userRepository.findByUsernameField("Example user")).thenReturn(user)
    val authentication = UsernamePasswordAuthenticationToken("Example user", null, emptyList())
    SecurityContextHolder.getContext().authentication = authentication
  }

  @Test
  fun `should successfully create a reading session`() {
    val savedSession = reading.copy(id = 1)
    `when`(sessionRepository.save(any())).thenReturn(savedSession)

    val result = readingService.createReadingSession(readingRequest, 1)

    assertNotNull(result)
    assertEquals(reading.book.id, result.bookId)
    assertEquals(reading.startReadingDate, result.startReadingDate)
    assertEquals(reading.totalProgress, result.totalProgress)
    assertEquals(reading.progressInPercentage, result.progressInPercentage)
    assertEquals(reading.book.title, result.bookTitle)
    assertEquals(reading.pages, result.pages)
    assertEquals(reading.chapters, result.chapters)

    verify(sessionRepository, times(1)).save(any())
  }

  @Test
  fun `should throw exception for invalid reading session creation`() {

    val invalidSession = readingRequest.copy(
      trackingMethod = TrackingMethod.CHAPTERS,
      dailyGoal = -1,
      startReadingDate = LocalDateTime.now().plusDays(1),
    )

    val exception = assertThrows<ReadingSessionNotValidException> {
      readingService.createReadingSession(invalidSession, 1)
    }

    val expectedErrors = listOf(
      ValidationErrorMessages.BOOK_HAS_NO_CHAPTERS.message,
      ValidationErrorMessages.NEGATIVE_DAILY_GOAL.message,
      ValidationErrorMessages.FUTURE_START_READING.message,
    )

    assertEquals(expectedErrors, exception.errors)
    assertEquals(ExceptionErrorMessages.READING_NOT_VALID.message, exception.message)

    verify(sessionRepository, never()).save(any())
  }

  @Test
  fun `should throw exception for non-existing book during reading creation`() {
    val invalidBookId = 99

    val bookRepository = mock(BookRepository::class.java, withSettings().extraInterfaces(ExceptionProvider::class.java))
    `when`(bookRepository.findById(invalidBookId)).thenReturn(Optional.empty())
    `when`((bookRepository as ExceptionProvider<Int>).notFoundException(invalidBookId))
      .thenReturn(BookNotFoundException(invalidBookId))

    val sessionRepository = mock(ReadingSessionRepository::class.java)
    val logRepository = mock(ReadingLogRepository::class.java)

    @Suppress("UNCHECKED_CAST")
    val logValidator = mock(Validator::class.java) as Validator<ReadingLog>

    @Suppress("UNCHECKED_CAST")
    val readingValidator = mock(Validator::class.java) as Validator<ReadingSession>
    val userRepository = mock(UserRepository::class.java)

    val readingService = ReadingService(
      sessionRepository,
      logRepository,
      bookRepository,
      logValidator,
      readingValidator,
      userRepository
    )

    val readingInvalid = readingRequest.copy()

    val exception = assertThrows(BookNotFoundException::class.java) {
      readingService.createReadingSession(readingInvalid, invalidBookId)
    }

    assertEquals("Book ID=$invalidBookId not found.", (exception as BookNotFoundException).apiMessage)
    verify(sessionRepository, never()).save(any())
  }


  @Test
  fun `should return the correct reading session by id`() {
    `when`(sessionRepository.findById(reading.id)).thenReturn(Optional.of(reading))

    val result = readingService.getReadingSessionById(reading.id)

    assertEquals(result.id, reading.id)
    assertEquals(result.totalProgress, reading.totalProgress)
    assertEquals(result.progressInPercentage, reading.progressInPercentage)
    assertEquals(result.dailyGoal, reading.dailyGoal)
    assertEquals(result.startReadingDate, reading.startReadingDate)
    assertEquals(result.trackingMethod, reading.trackingMethod)

    verify(sessionRepository, times(1)).findById(1)
  }

  @Test
  fun `should throw exception for invalid reading session id`() {
    `when`(sessionRepository.findById(2)).thenReturn(Optional.empty())

    val exception = assertThrows<ReadingSessionNotFoundException> { readingService.getReadingSessionById(2) }

    assert(exception.message == ExceptionErrorMessages.READING_NOT_FOUND.message)
    verify(sessionRepository, times(1)).findById(2)
  }

  @Test
  fun `should return list of readings for a valid book id`() {
    val readings = listOf(reading, reading.copy(), reading.copy())
    `when`(sessionRepository.findByBookId(book.id)).thenReturn(readings)

    val result = readingService.getReadingSessionsByBookId(book.id)

    assert(result.size == readings.size)
    assert(result[0].bookTitle == "Example book")
    verify(sessionRepository, times(1)).findByBookId(book.id)
  }

  @Test
  fun `should return an empty list when no readings are found`() {
    `when`(sessionRepository.findByBookId(1)).thenReturn(emptyList())
    val result = readingService.getReadingSessionsByBookId(1)

    assert(result.isEmpty())
    verify(sessionRepository, times(1)).findByBookId(1)
  }

  @Test
  fun `should throw exception for invalid book id during reading search`() {
    val invalidBookId = 99

    val bookRepository = mock(BookRepository::class.java, withSettings().extraInterfaces(ExceptionProvider::class.java))
    `when`(bookRepository.findById(invalidBookId)).thenReturn(Optional.empty())
    `when`((bookRepository as ExceptionProvider<Int>).notFoundException(invalidBookId))
      .thenReturn(BookNotFoundException(invalidBookId))

    val sessionRepository = mock(ReadingSessionRepository::class.java)
    val logRepository = mock(ReadingLogRepository::class.java)

    @Suppress("UNCHECKED_CAST")
    val logValidator = mock(Validator::class.java) as Validator<ReadingLog>

    @Suppress("UNCHECKED_CAST")
    val readingValidator = mock(Validator::class.java) as Validator<ReadingSession>
    val userRepository = mock(UserRepository::class.java)

    val readingService = ReadingService(
      sessionRepository,
      logRepository,
      bookRepository,
      logValidator,
      readingValidator,
      userRepository
    )

    val exception = assertThrows(BookNotFoundException::class.java) {
      readingService.getReadingSessionsByBookId(invalidBookId)
    }

    assertEquals("Book ID=$invalidBookId not found.", (exception as ApiException).apiMessage)
    verify(sessionRepository, never()).save(any())
  }


  @Test
  fun `should successfully add a reading log`() {
    `when`(sessionRepository.findById(reading.id)).thenReturn(Optional.of(reading))
    `when`(logRepository.save(any())).thenReturn(readingLog)
    `when`(sessionRepository.save(any())).thenReturn(reading)

    val result = readingService.addReading(readingLog.readingSession.id, readingLog.quantityRead)

    assertEquals(readingLog.id, result.id)
    assertEquals(readingLog.quantityRead, result.quantityRead)
    assertEquals(readingLog.dateOfReading.truncatedTo(ChronoUnit.MINUTES), result.dateOfReading)
    assertEquals(readingLog.readingSession.id, result.readingSessionId)

    verify(sessionRepository, times(1)).findById(reading.id)
    verify(logRepository, times(1)).save(any())
    verify(sessionRepository, times(1)).save(any())
  }

  @Test
  fun `should throw exception for invalid reading log`() {
    val invalidLog = readingLog.copy(quantityRead = -10)

    `when`(sessionRepository.findById(reading.id)).thenReturn(Optional.of(reading))

    val exception = assertThrows<InvalidReadingLogException> {
      readingService.addReading(invalidLog.readingSession.id, invalidLog.quantityRead)
    }

    assertEquals(ExceptionErrorMessages.LOG_WITH_INVALID_VALUE.message, exception.message)
    verify(logRepository, never()).save(invalidLog)
    verify(sessionRepository, never()).save(invalidLog.readingSession)
  }

  @Test
  fun `should throw exception for non-existing reading session when adding log`() {
    val spyReadingService = spy(readingService)
    doThrow(ReadingSessionNotFoundException()).`when`(spyReadingService).getReadingSessionById(99)

    val exception = assertThrows<ReadingSessionNotFoundException> {
      spyReadingService.addReading(99, 10)
    }

    assert(exception.message == ExceptionErrorMessages.READING_NOT_FOUND.message)
    verify(spyReadingService, times(1)).getReadingSessionById(99)
    verify(logRepository, times(0)).save(readingLog)
  }

  @Test
  fun `should delete reading session successfully`() {
    val readingId = 1
    val readingSession = reading.copy(id = readingId)

    `when`(sessionRepository.findById(readingId)).thenReturn(Optional.of(readingSession))

    readingService.deleteReadingById(readingId)

    verify(sessionRepository, times(1)).findById(readingId)
    verify(sessionRepository, times(1)).deleteById(readingId)
  }

  @Test
  fun `should throw exception when reading session not found`() {
    val readingId = 99

    `when`(sessionRepository.findById(readingId)).thenReturn(Optional.empty())

    val exception = assertThrows<EntityNotFoundException> {
      readingService.deleteReadingById(readingId)
    }

    assertEquals("ReadingSession with id: $readingId does not exist.", exception.message)
    verify(sessionRepository, times(1)).findById(readingId)
    verify(sessionRepository, never()).deleteById(any())
  }
}
