package dev.wayron.book_tracker_api.modules.exceptions.reading

import dev.wayron.book_tracker_api.modules.exceptions.ApiException
import dev.wayron.book_tracker_api.modules.exceptions.ExceptionErrorMessages
import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus

class ReadingSessionNotFoundException(id: Int? = null) : EntityNotFoundException(ExceptionErrorMessages.READING_NOT_FOUND.message),
  ApiException {
  override val apiMessage = if(id != null) {
    "Reading Session ID=$id not found."
  } else {
    ExceptionErrorMessages.READING_NOT_FOUND.message
  }
  override val status = HttpStatus.NOT_FOUND
  override val details = emptyList<String>()
}
