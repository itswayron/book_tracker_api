package dev.wayron.book_tracker_api.entities.reading

import com.fasterxml.jackson.databind.ObjectMapper
import dev.wayron.book_tracker_api.entities.reading.model.dto.ReadingSessionDTO
import dev.wayron.book_tracker_api.entities.reading.model.dto.ReadingSessionRequest
import dev.wayron.book_tracker_api.entities.reading.model.enums.ReadingState
import dev.wayron.book_tracker_api.entities.reading.model.enums.TrackingMethod
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
          request.startReadingDate?.truncatedTo(ChronoUnit.MINUTES).toString()
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

}