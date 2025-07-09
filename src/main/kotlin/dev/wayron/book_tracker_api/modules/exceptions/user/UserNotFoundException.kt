package dev.wayron.book_tracker_api.modules.exceptions.user

import dev.wayron.book_tracker_api.modules.exceptions.ApiException
import dev.wayron.book_tracker_api.modules.exceptions.ExceptionErrorMessages
import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus

class UserNotFoundException(id: String? = null) : EntityNotFoundException(ExceptionErrorMessages.USER_NOT_FOUND.message),
  ApiException {
  override val apiMessage: String = if(id != null) {
    "Username $id not found."
  } else {
    ExceptionErrorMessages.USER_NOT_FOUND.message
  }
  override val status: HttpStatus = HttpStatus.NOT_FOUND
  override val details: List<String?> = emptyList<String>()
}