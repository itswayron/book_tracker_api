package dev.wayron.book_tracker_api.modules.exceptions

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus

interface ApiException {
  val apiMessage: String
  val status: HttpStatus
  val details: List<String?>

  fun toApiError(request: HttpServletRequest): ApiError {
    return ApiError(
      status = status.value(),
      error = status.reasonPhrase,
      message = apiMessage,
      path = request.requestURI,
      details = details
    )
  }
}