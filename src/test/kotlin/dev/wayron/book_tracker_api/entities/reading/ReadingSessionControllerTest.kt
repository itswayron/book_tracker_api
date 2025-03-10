package dev.wayron.book_tracker_api.entities.reading

import com.fasterxml.jackson.databind.ObjectMapper
import dev.wayron.book_tracker_api.entities.reading.model.dto.AddReadingRequest
import dev.wayron.book_tracker_api.entities.reading.model.dto.ReadingLogDTO
import dev.wayron.book_tracker_api.entities.reading.model.dto.ReadingSessionDTO
import dev.wayron.book_tracker_api.entities.reading.model.dto.ReadingSessionRequest
import dev.wayron.book_tracker_api.entities.reading.model.enums.ReadingState
import dev.wayron.book_tracker_api.entities.reading.model.enums.TrackingMethod
import dev.wayron.book_tracker_api.exceptions.ExceptionErrorMessages
import dev.wayron.book_tracker_api.exceptions.book.BookNotFoundException
import dev.wayron.book_tracker_api.exceptions.logs.InvalidReadingLogException
import dev.wayron.book_tracker_api.exceptions.readingSession.ReadingSessionNotFoundException
import dev.wayron.book_tracker_api.exceptions.readingSession.ReadingSessionNotValidException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@ExtendWith(SpringExtension::class)
@WebMvcTest(ReadingSessionController::class)
class ReadingSessionControllerTest {

  @Autowired
  private lateinit var mockMvc: MockMvc

  @Autowired
  private lateinit var mapper: ObjectMapper

  @MockitoBean
  private lateinit var service: ReadingService
  private lateinit var request: ReadingSessionRequest
  private lateinit var requestJson: String
  private lateinit var requestDTO: ReadingSessionDTO
  private lateinit var logDTO: ReadingLogDTO

  @BeforeEach
  fun setUp() {
    request = ReadingSessionRequest(
      dailyGoal = 10,
      trackingMethod = TrackingMethod.PAGES,
      bookId = 1
    )
    requestJson = mapper.writeValueAsString(request)
    requestDTO = ReadingSessionDTO(
      id = 1,
      bookId = 1,
      bookTitle = "Example author",
      progressInPercentage = 0.0,
      totalProgress = 0,
      pages = 300,
      chapters = 10,
      readingState = ReadingState.READING,
      dailyGoal = 10,
      startReadingDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES),
      endReadingDate = null,
      estimatedCompletionDate = null
    )
    logDTO = ReadingLogDTO(
      id = 1,
      readingSessionId = 1,
      bookId = 1,
      dateOfReading = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES),
      quantityRead = 10
    )
  }

  @Test
  fun `should successfully create a reading`() {
    `when`(service.createReadingSession(request)).thenReturn(requestDTO)

    mockMvc.perform(
      post("/readings/${request.bookId}")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestJson)
    ).andExpect(status().isCreated)
      .andExpect(jsonPath("$.bookId").value(request.bookId))
      .andExpect(
        jsonPath("$.startReadingDate").value(
          requestDTO.startReadingDate.truncatedTo(ChronoUnit.MINUTES).toString()
        )
      )
      .andExpect(jsonPath("$.dailyGoal").value(request.dailyGoal))
  }

  @Test
  fun `should return bad request for invalid reading creation`() {
    val invalidRequest = request.copy(dailyGoal = -10, trackingMethod = TrackingMethod.CHAPTERS)
    `when`(service.createReadingSession(invalidRequest)).thenThrow(ReadingSessionNotValidException(listOf()))

    mockMvc.perform(
      post("/readings/${request.bookId}")
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(invalidRequest))
    ).andExpect(status().isBadRequest)
  }

  @Test
  fun `should return 404 for non-existent book during creation`() {
    `when`(service.createReadingSession(request.copy(bookId = 99))).thenThrow(BookNotFoundException())

    mockMvc.perform(
      post("/readings/99")
        .contentType(MediaType.APPLICATION_JSON)
        .content(
          mapper.writeValueAsString(request.copy(bookId = 99))
        )
    ).andExpect(status().isNotFound)
  }

  @Test
  fun `should return list of readings for a valid book id`() {
    `when`(service.getReadingSessionsByBookId(request.bookId!!)).thenReturn(listOf(requestDTO))

    mockMvc.perform(
      get("/readings/1")
        .contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isOk)
      .andExpect(jsonPath("$.size()").value(1))
      .andExpect(jsonPath("$[0].id").value(requestDTO.id))
      .andExpect(jsonPath("$[0].bookId").value(requestDTO.bookId))
      .andExpect(jsonPath("$[0].bookTitle").value(requestDTO.bookTitle))
      .andExpect(jsonPath("$[0].progressInPercentage").value(requestDTO.progressInPercentage))
      .andExpect(jsonPath("$[0].totalProgress").value(requestDTO.totalProgress))
      .andExpect(
        jsonPath("$[0].dailyGoal").value(requestDTO.dailyGoal)
      )
  }

  @Test
  fun `should return empty list when no sessions are found`() {
    `when`(service.getReadingSessionsByBookId(2)).thenReturn(emptyList())

    mockMvc.perform(
      get("/readings/2")
        .contentType(MediaType.APPLICATION_JSON)
    )
      .andExpect(status().isOk)
      .andExpect(jsonPath("$.size()").value(0))
  }

  @Test
  fun `should successfully add a reading log`() {
    `when`(service.addReading(logDTO.readingSessionId, logDTO.quantityRead)).thenReturn(logDTO)

    mockMvc.perform(
      post("/readings/add/${logDTO.readingSessionId}")
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(AddReadingRequest(logDTO.quantityRead)))
    )
      .andExpect(status().isOk)
      .andExpect(jsonPath("$.id").value(logDTO.id))
      .andExpect(jsonPath("$.readingSessionId").value(logDTO.readingSessionId))
      .andExpect(jsonPath("$.quantityRead").value(logDTO.quantityRead))
      .andExpect(jsonPath("$.dateOfReading").value(logDTO.dateOfReading.toString()))
      .andExpect(jsonPath("$.bookId").value(logDTO.bookId))
  }

  @Test
  fun `should return bad request for invalid log creation`() {
    `when`(service.addReading(logDTO.readingSessionId, -10)).thenThrow(InvalidReadingLogException())

    mockMvc.perform(
      post("/readings/add/${logDTO.readingSessionId}")
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(AddReadingRequest(-10)))
    )
      .andExpect(status().isBadRequest)
      .andExpect(jsonPath("$.message").value(ExceptionErrorMessages.LOG_WITH_INVALID_VALUE.message))
  }

  @Test
  fun `should return 404 when for non-existing reading session when adding log`() {
    `when`(service.addReading(2, 10)).thenThrow(ReadingSessionNotFoundException())

    mockMvc.perform(
      post("/readings/add/2")
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(AddReadingRequest(10)))
    ).andExpect(status().isNotFound)
  }

}