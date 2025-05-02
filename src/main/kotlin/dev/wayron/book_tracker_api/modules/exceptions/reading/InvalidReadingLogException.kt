package dev.wayron.book_tracker_api.modules.exceptions.reading

import dev.wayron.book_tracker_api.modules.exceptions.ApiException
import dev.wayron.book_tracker_api.modules.exceptions.ExceptionErrorMessages
import org.springframework.http.HttpStatus

class InvalidReadingLogException : IllegalStateException(ExceptionErrorMessages.LOG_WITH_INVALID_VALUE.message),
  ApiException {
  override val apiMessage = ExceptionErrorMessages.LOG_WITH_INVALID_VALUE.message
  override val status = HttpStatus.BAD_REQUEST
  override val details = emptyList<String>()
}