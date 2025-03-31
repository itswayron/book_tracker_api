package dev.wayron.book_tracker_api.modules.exceptions

import dev.wayron.book_tracker_api.modules.exceptions.book.BookNotFoundException
import dev.wayron.book_tracker_api.modules.exceptions.book.BookNotValidException
import dev.wayron.book_tracker_api.modules.exceptions.reading.ForbiddenActionException
import dev.wayron.book_tracker_api.modules.exceptions.reading.InvalidReadingLogException
import dev.wayron.book_tracker_api.modules.exceptions.reading.ReadingSessionCompletedException
import dev.wayron.book_tracker_api.modules.exceptions.reading.ReadingSessionNotFoundException
import dev.wayron.book_tracker_api.modules.exceptions.reading.ReadingSessionNotValidException
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
    val status = when (exception) {
      is BookNotValidException -> HttpStatus.BAD_REQUEST
      is BookNotFoundException -> HttpStatus.NOT_FOUND
      is ReadingSessionNotValidException -> HttpStatus.BAD_REQUEST
      is ReadingSessionNotFoundException -> HttpStatus.NOT_FOUND
      is ReadingSessionCompletedException -> HttpStatus.CONFLICT
      is InvalidReadingLogException -> HttpStatus.BAD_REQUEST
      is ForbiddenActionException -> HttpStatus.UNAUTHORIZED
      else -> HttpStatus.INTERNAL_SERVER_ERROR
    }

    val error = status.reasonPhrase

    val details = when (exception) {
      is ReadingSessionNotValidException -> exception.errors
      is BookNotValidException -> exception.errors
      else -> arrayListOf(exception.message)
    }

    val apiError = ApiError(
      status = status.value(),
      message = exception.message ?: "An error occurred.",
      error = error,
      path = request.requestURI,
      details = details,
    )

    return ResponseEntity(apiError, status)
  }

  @ExceptionHandler(HttpMessageNotReadableException::class)
  @ResponseBody
  fun handleBookNotValidException(
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