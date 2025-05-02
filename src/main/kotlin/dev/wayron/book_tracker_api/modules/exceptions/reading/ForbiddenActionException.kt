package dev.wayron.book_tracker_api.modules.exceptions.reading

import dev.wayron.book_tracker_api.modules.exceptions.ApiException
import dev.wayron.book_tracker_api.modules.exceptions.ExceptionErrorMessages
import org.springframework.http.HttpStatus

class ForbiddenActionException : RuntimeException(ExceptionErrorMessages.FORBIDDEN_ACCESS.message), ApiException {
  override val apiMessage = ExceptionErrorMessages.FORBIDDEN_ACCESS.message
  override val status = HttpStatus.UNAUTHORIZED
  override val details = emptyList<String>()
}