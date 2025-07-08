package dev.wayron.book_tracker_api.modules.exceptions

import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus

class UserNotFoundException(id: String? = null) : EntityNotFoundException(ExceptionErrorMessages.USER_NOT_FOUND.message), ApiException {
  override val apiMessage: String = if(id != null) {
    "Username $id not found."
  } else {
    ExceptionErrorMessages.USER_NOT_FOUND.message
  }
  override val status: HttpStatus = HttpStatus.NOT_FOUND
  override val details: List<String?> = emptyList<String>()
}
