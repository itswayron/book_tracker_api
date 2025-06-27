package dev.wayron.book_tracker_api.modules.controllers.security

import dev.wayron.book_tracker_api.modules.models.security.AuthenticationRequest
import dev.wayron.book_tracker_api.modules.models.security.AuthenticationResponse
import dev.wayron.book_tracker_api.modules.services.security.AuthenticationService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/login")
class AuthController(
  private val authenticationService: AuthenticationService
) {

  @PostMapping
  fun authenticate(@RequestBody authRequest: AuthenticationRequest): AuthenticationResponse =
    authenticationService.authentication(authRequest)
}