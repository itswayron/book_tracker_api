package dev.wayron.book_tracker_api.modules.exceptions.book

import dev.wayron.book_tracker_api.modules.exceptions.ApiException
import dev.wayron.book_tracker_api.modules.exceptions.ExceptionErrorMessages
import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus

class BookNotFoundException() : EntityNotFoundException(ExceptionErrorMessages.BOOK_NOT_FOUND.message), ApiException {
  override val apiMessage = ExceptionErrorMessages.BOOK_NOT_FOUND.message
  override val status = HttpStatus.NOT_FOUND
  override val details = emptyList<String>()
}