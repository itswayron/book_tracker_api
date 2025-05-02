package dev.wayron.book_tracker_api.modules.exceptions

import dev.wayron.book_tracker_api.modules.exceptions.book.BookNotFoundException
import dev.wayron.book_tracker_api.modules.exceptions.book.BookNotValidException
import dev.wayron.book_tracker_api.modules.exceptions.reading.*
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody

@ControllerAdvice
class GlobalExceptionHandler {

  @ExceptionHandler(
    BookNotValidException::class,
    BookNotFoundException::class,
    ReadingSessionNotValidException::class,
    ReadingSessionNotFoundException::class,
    ReadingSessionCompletedException::class,
    InvalidReadingLogException::class,
    ForbiddenActionException::class,
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

    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseBody
    fun handleInvalidJsonException(
      exception: Exception,
      request: HttpServletRequest
    ): ResponseEntity<ApiError> {
      val status = HttpStatus.BAD_REQUEST
      val error = HttpStatus.BAD_REQUEST.reasonPhrase
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
}