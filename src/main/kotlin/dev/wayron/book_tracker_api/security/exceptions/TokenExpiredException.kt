package dev.wayron.book_tracker_api.security.exceptions

import dev.wayron.book_tracker_api.modules.exceptions.ApiException
import dev.wayron.book_tracker_api.modules.exceptions.ExceptionErrorMessages
import org.springframework.http.HttpStatus

class TokenExpiredException: RuntimeException(), ApiException {
  override val apiMessage: String = ExceptionErrorMessages.EXPIRED_RESET_PASSWORD_TOKEN.message
  override val status: HttpStatus = HttpStatus.BAD_REQUEST
  override val details: List<String?> = emptyList()
}