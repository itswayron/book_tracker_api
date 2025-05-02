package dev.wayron.book_tracker_api.modules.exceptions.reading

import dev.wayron.book_tracker_api.modules.exceptions.ApiException
import dev.wayron.book_tracker_api.modules.exceptions.ExceptionErrorMessages
import org.springframework.http.HttpStatus

class ReadingSessionNotValidException(val errors: List<String>) :
  IllegalArgumentException(ExceptionErrorMessages.READING_NOT_VALID.message), ApiException {
  override val apiMessage = ExceptionErrorMessages.READING_NOT_VALID.message
  override val status = HttpStatus.BAD_REQUEST
  override val details = errors
}