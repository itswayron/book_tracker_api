package dev.wayron.book_tracker_api.security.exceptions

import dev.wayron.book_tracker_api.modules.exceptions.ApiException
import dev.wayron.book_tracker_api.modules.exceptions.ExceptionErrorMessages
import org.springframework.http.HttpStatus

class InvalidPasswordException(val errors: List<String> = emptyList()) : IllegalArgumentException(), ApiException {
  override val apiMessage: String = ExceptionErrorMessages.INVALID_PASSWORD.message
  override val status: HttpStatus = HttpStatus.BAD_REQUEST
  override val details: List<String?> = errors
}