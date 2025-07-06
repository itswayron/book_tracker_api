package dev.wayron.book_tracker_api.modules.services.reading

import dev.wayron.book_tracker_api.modules.exceptions.ExceptionErrorMessages
import dev.wayron.book_tracker_api.modules.exceptions.book.BookNotFoundException
import dev.wayron.book_tracker_api.modules.exceptions.reading.InvalidReadingLogException
import dev.wayron.book_tracker_api.modules.exceptions.reading.ReadingSessionNotFoundException
import dev.wayron.book_tracker_api.modules.exceptions.reading.ReadingSessionNotValidException
import dev.wayron.book_tracker_api.modules.models.book.Book
import dev.wayron.book_tracker_api.modules.models.book.BookRequest
import dev.wayron.book_tracker_api.modules.models.book.BookResponse
import dev.wayron.book_tracker_api.modules.models.mappers.ReadingMapper
import dev.wayron.book_tracker_api.modules.models.reading.ReadingLog
import dev.wayron.book_tracker_api.modules.models.reading.ReadingSession
import dev.wayron.book_tracker_api.modules.models.reading.dto.ReadingSessionRequest
import dev.wayron.book_tracker_api.modules.models.reading.enums.ReadingState
import dev.wayron.book_tracker_api.modules.models.reading.enums.TrackingMethod
import dev.wayron.book_tracker_api.modules.repositories.reading.ReadingLogRepository
import dev.wayron.book_tracker_api.modules.repositories.reading.ReadingSessionRepository
import dev.wayron.book_tracker_api.modules.services.book.BookService
import dev.wayron.book_tracker_api.modules.validators.ValidationErrorMessages
import dev.wayron.book_tracker_api.modules.validators.Validator
import dev.wayron.book_tracker_api.modules.validators.reading.ReadingLogValidator
import dev.wayron.book_tracker_api.modules.validators.reading.ReadingSessionValidator
import dev.wayron.book_tracker_api.modules.repositories.user.UserRepository
import dev.wayron.book_tracker_api.modules.models.user.User
import dev.wayron.book_tracker_api.utils.Mappers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ReadingServiceTest {
  @Mock
  private val sessionRepository: ReadingSessionRepository = mock(ReadingSessionRepository::class.java)

  @Mock
  private val logRepository: ReadingLogRepository = mock(ReadingLogRepository::class.java)

  @Mock
  private val bookService: BookService = mock(BookService::class.java)

  @Mock
  private val logValidator: Validator<ReadingLog> = ReadingLogValidator()

  @Mock
  private val readingValidator: Validator<ReadingSession> = ReadingSessionValidator()

  @Mock
  private val mapper: ReadingMapper = mock(ReadingMapper::class.java)

  @Mock
  private val userRepository: UserRepository = mock(UserRepository::class.java)

  @Mock
  private val userAccessValidator: UserAccessValidator = mock(UserAccessValidator::class.java)

  @InjectMocks
  private val readingService =
    ReadingService(
      sessionRepository,
      logRepository,
      bookService,
      logValidator,
      readingValidator,
      mapper,
      userRepository,
      userAccessValidator,
    )

  private lateinit var book: Book
  private lateinit var reading: ReadingSession
  private lateinit var readingRequest: ReadingSessionRequest
  private lateinit var readingLog: ReadingLog
  private lateinit var user: User
  private lateinit var bookRequest: BookRequest
  private lateinit var bookResponse: BookResponse

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
      bookId = book.id,
      trackingMethod = reading.trackingMethod,
      dailyGoal = reading.dailyGoal,
      startReadingDate = reading.startReadingDate,
      estimatedCompletionDate = reading.estimatedCompletionDate,
    )
    readingLog = ReadingLog(
      id = 0,
      readingSession = reading,
      quantityRead = 10,
      userId = user
    )
    `when`(bookService.getBookById(book.id)).thenReturn(book)
  }

  @Test
  fun `should successfully create a reading session`() {

    val savedSession = reading.copy(id = 1)
    `when`(sessionRepository.save(any<ReadingSession>())).thenReturn(savedSession)

    val result = readingService.createReadingSession(readingRequest)

    assertNotNull(result)
    assertEquals(reading.book.id, result.bookId)
    assertEquals(reading.startReadingDate, result.startReadingDate)
    assertEquals(reading.totalProgress, result.totalProgress)
    assertEquals(reading.progressInPercentage, result.progressInPercentage)
    assertEquals(reading.book.title, result.bookTitle)
    assertEquals(reading.pages, result.pages)
    assertEquals(reading.chapters, result.chapters)

    verify(sessionRepository, times(1)).save(any<ReadingSession>())
  }

  @Test
  fun `should throw exception for invalid reading session creation`() {

    val invalidSession = reading.copy(
      trackingMethod = TrackingMethod.CHAPTERS,
      dailyGoal = -1,
      startReadingDate = LocalDateTime.now().plusDays(1),
    )

    val exception = assertThrows<ReadingSessionNotValidException> {
      readingService.createReadingSession(Mappers.mapReadingSessionToRequest(invalidSession))
    }

    val expectedErrors = listOf(
      ValidationErrorMessages.BOOK_HAS_NO_CHAPTERS.message,
      ValidationErrorMessages.NEGATIVE_DAILY_GOAL.message,
      ValidationErrorMessages.FUTURE_START_READING.message,
    )

    assertEquals(expectedErrors, exception.errors)
    assertEquals(ExceptionErrorMessages.READING_NOT_VALID.message, exception.message)

    verify(sessionRepository, never()).save(any<ReadingSession>())
  }

  @Test
  fun `should throw exception for non-existing book during reading creation`() {
    val readingInvalid = reading.copy(book = book.copy(id = 99))
    `when`(bookService.getBookById(readingInvalid.book.id)).thenThrow(BookNotFoundException())

    val exception = assertThrows<BookNotFoundException> {
      readingService.createReadingSession(Mappers.mapReadingSessionToRequest(readingInvalid))
    }

    assertEquals(ExceptionErrorMessages.BOOK_NOT_FOUND.message, exception.apiMessage)
    verify(sessionRepository, never()).save(any<ReadingSession>())
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
    `when`(sessionRepository.findById(1)).thenReturn(Optional.of(reading))
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
    `when`(bookService.getBookById(99)).thenThrow(BookNotFoundException())

    val exception = assertThrows<BookNotFoundException> { readingService.getReadingSessionsByBookId(99) }

    assertEquals(ExceptionErrorMessages.BOOK_NOT_FOUND.message, exception.apiMessage)
    verify(sessionRepository, never()).save(any<ReadingSession>())
  }

  @Test
  fun `should successfully add a reading log`() {
    `when`(sessionRepository.findById(reading.id)).thenReturn(Optional.of(reading))
    `when`(logRepository.save(any<ReadingLog>())).thenReturn(readingLog)
    `when`(sessionRepository.save(any<ReadingSession>())).thenReturn(reading)

    val result = readingService.addReading(readingLog.readingSession.id, readingLog.quantityRead)

    assertEquals(readingLog.id, result.id)
    assertEquals(readingLog.quantityRead, result.quantityRead)
    assertEquals(readingLog.dateOfReading.truncatedTo(ChronoUnit.MINUTES), result.dateOfReading)
    assertEquals(readingLog.readingSession.id, result.readingSessionId)

    verify(sessionRepository, times(1)).findById(reading.id)
    verify(logRepository, times(1)).save(any<ReadingLog>())
    verify(sessionRepository, times(1)).save(any<ReadingSession>())
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
}