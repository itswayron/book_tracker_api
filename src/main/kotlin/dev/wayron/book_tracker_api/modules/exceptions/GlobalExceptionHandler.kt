package dev.wayron.book_tracker_api.modules.exceptions

import dev.wayron.book_tracker_api.modules.exceptions.book.BookNotFoundException
import dev.wayron.book_tracker_api.modules.exceptions.book.BookNotValidException
import dev.wayron.book_tracker_api.modules.exceptions.reading.*
import dev.wayron.book_tracker_api.modules.exceptions.user.UserNotFoundException
import dev.wayron.book_tracker_api.modules.exceptions.user.UserNotValidException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.multipart.MaxUploadSizeExceededException

@RestControllerAdvice
class GlobalExceptionHandler {

  @ExceptionHandler(
    BookNotValidException::class,
    BookNotFoundException::class,
    ReadingSessionNotValidException::class,
    ReadingSessionNotFoundException::class,
    ReadingSessionCompletedException::class,
    InvalidReadingLogException::class,
    ForbiddenActionException::class,
    ImageNotValidException::class,
    UserNotValidException::class,
    UserNotFoundException::class,
  )
  @ResponseBody
  fun handleCustomExceptions(exception: Exception, request: HttpServletRequest): ResponseEntity<ApiError> {
    return if (exception is ApiException) {
      val apiError = exception.toApiError(request)
      ResponseEntity(apiError, HttpStatus.valueOf(apiError.status))
    } else {
      val status = HttpStatus.INTERNAL_SERVER_ERROR
      val apiError = ApiError(
        status = status.value(),
        error = status.reasonPhrase,
        message = exception.message ?: "An error occurred.",
        path = request.requestURI,
        details = arrayListOf(exception.message),
      )
      ResponseEntity(apiError, status)
    }
  }

  @ExceptionHandler(Exception::class)
  @ResponseBody
  fun handleUnknownException(exception: Exception, request: HttpServletRequest): ResponseEntity<ApiError> {
    val status = HttpStatus.INTERNAL_SERVER_ERROR
    val apiError = ApiError(
      status = status.value(),
      error = status.reasonPhrase,
      message = exception.message ?: "An unexpected error occurred.",
      path = request.requestURI,
      details = arrayListOf(exception.localizedMessage)
    )
    return ResponseEntity(apiError, status)
  }

  @ExceptionHandler(MaxUploadSizeExceededException::class)
  @ResponseBody
  fun handleMaxUploadSizeExceededException(
    exception: MaxUploadSizeExceededException,
    request: HttpServletRequest
  ): ResponseEntity<ApiError> {
    val status = HttpStatus.PAYLOAD_TOO_LARGE
    val apiError = ApiError(
      status = status.value(),
      error = status.reasonPhrase,
      message = "The uploaded file exceeds the maximum allowed size of 5MB.",
      path = request.requestURI,
      details = arrayListOf(exception.localizedMessage)
    )
    return ResponseEntity(apiError, status)
  }

  @ExceptionHandler(HttpMessageNotReadableException::class)
  @ResponseBody
  fun handleInvalidJsonException(
    exception: Exception,
    request: HttpServletRequest
  ): ResponseEntity<ApiError> {
    val status = HttpStatus.BAD_REQUEST
    val error = status.reasonPhrase
    val message = BookNotValidException(arrayListOf()).message ?: "An error occurred."
    val details = arrayListOf("Invalid JSON structure")

    val apiError = ApiError(
      status = status.value(),
      error = error,
      message = message,
      path = request.requestURI,
      details = details
    )
    return ResponseEntity(apiError, status)
  }
}
