package dev.wayron.book_tracker_api.modules.exceptions

import org.springframework.http.HttpStatus

class ImageNotValidException(val errors: List<String>) : RuntimeException(), ApiException {
  override val apiMessage: String = ExceptionErrorMessages.INVALID_IMAGE.message
  override val status: HttpStatus = HttpStatus.BAD_REQUEST
  override val details: List<String?> = errors
}