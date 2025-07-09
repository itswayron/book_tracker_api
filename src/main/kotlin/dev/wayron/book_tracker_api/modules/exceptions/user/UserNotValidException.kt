package dev.wayron.book_tracker_api.modules.exceptions.user

import dev.wayron.book_tracker_api.modules.exceptions.ApiException
import dev.wayron.book_tracker_api.modules.exceptions.ExceptionErrorMessages
import org.springframework.http.HttpStatus

class UserNotValidException(name: String? = null, errors : List<String> = emptyList()) : IllegalArgumentException(ExceptionErrorMessages.USER_NOT_VALID.message), ApiException {
  override val apiMessage: String = if(name != null) {
    "User name=$name not valid."
  } else {
    ExceptionErrorMessages.USER_NOT_VALID.message
  }
  override val status: HttpStatus = HttpStatus.BAD_REQUEST
  override val details: List<String?> = errors
}