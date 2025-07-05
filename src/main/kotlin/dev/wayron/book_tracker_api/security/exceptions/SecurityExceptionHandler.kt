package dev.wayron.book_tracker_api.security.exceptions

import dev.wayron.book_tracker_api.modules.exceptions.ApiError
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class SecurityExceptionHandler {

  @ExceptionHandler(AccessDeniedException::class)
  @ResponseBody
  fun handleAccessDeniedException(
    exception: AccessDeniedException,
    request: HttpServletRequest,
  ): ResponseEntity<ApiError> =
    ResponseEntity.status(HttpStatus.FORBIDDEN).body(
      ApiError(
        status = HttpStatus.FORBIDDEN.value(),
        error = HttpStatus.FORBIDDEN.reasonPhrase,
        message = "User does not have permission to perform this action.",
        path = request.requestURI
      )
    )
}