package dev.wayron.book_tracker_api.security.controller

import dev.wayron.book_tracker_api.security.controller.model.AuthenticationRequest
import dev.wayron.book_tracker_api.security.controller.model.AuthenticationResponse
import dev.wayron.book_tracker_api.security.controller.model.RefreshTokenRequest
import dev.wayron.book_tracker_api.security.controller.model.TokenResponse
import dev.wayron.book_tracker_api.security.service.AuthenticationService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/login")
class AuthController(
  private val authenticationService: AuthenticationService
) {

  @PostMapping
  fun authenticate(@RequestBody authRequest: AuthenticationRequest): AuthenticationResponse =
    authenticationService.authentication(authRequest)

  @PostMapping("/refresh")
  fun refreshAccessToken(
    @RequestBody request: RefreshTokenRequest
  ): TokenResponse =
    authenticationService.refreshAccessToken(request.token)?.mapTokenResponse() ?: throw ResponseStatusException(
      HttpStatus.FORBIDDEN,
      "Invalid refresh token!",
    )

  private fun String.mapTokenResponse(): TokenResponse = TokenResponse(this)
}