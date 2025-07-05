package dev.wayron.book_tracker_api.security.controllers


import dev.wayron.book_tracker_api.config.ApiRoutes
import dev.wayron.book_tracker_api.security.models.AuthenticationRequest
import dev.wayron.book_tracker_api.security.models.AuthenticationResponse
import dev.wayron.book_tracker_api.security.services.AuthenticationService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(ApiRoutes.AUTH)
class AuthController(
  private val authenticationService: AuthenticationService
) {

  @PostMapping
  fun authenticate(@RequestBody authRequest: AuthenticationRequest): AuthenticationResponse =
    authenticationService.authentication(authRequest)
}