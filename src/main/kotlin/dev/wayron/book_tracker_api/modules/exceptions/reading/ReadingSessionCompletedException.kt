package dev.wayron.book_tracker_api.modules.exceptions.reading

import dev.wayron.book_tracker_api.modules.exceptions.ApiException
import dev.wayron.book_tracker_api.modules.exceptions.ExceptionErrorMessages
import org.springframework.http.HttpStatus

class ReadingSessionCompletedException : IllegalStateException(ExceptionErrorMessages.READING_ALREADY_FINISHED.message),
  ApiException {
  override val apiMessage = ExceptionErrorMessages.READING_ALREADY_FINISHED.message
  override val status = HttpStatus.CONFLICT
  override val details = emptyList<String>()
}