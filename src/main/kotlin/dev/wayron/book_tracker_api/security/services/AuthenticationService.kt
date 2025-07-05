package dev.wayron.book_tracker_api.security.services

import dev.wayron.book_tracker_api.security.config.JwtProperties
import dev.wayron.book_tracker_api.security.models.AuthenticationRequest
import dev.wayron.book_tracker_api.security.models.AuthenticationResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthenticationService(
  private val authManager: AuthenticationManager,
  private val userService: UserService,
  private val tokenService: TokenService,
  private val jwtProperties: JwtProperties,
) {

  fun authentication(authRequest: AuthenticationRequest): AuthenticationResponse {
    authManager.authenticate(
      UsernamePasswordAuthenticationToken(authRequest.username, authRequest.password)
    )
    val user = userService.loadUserByUsername(authRequest.username)
    val accessToken = generateAccessToken(user)
    return AuthenticationResponse(accessToken)
  }

  private fun generateAccessToken(user: UserDetails) = tokenService.generate(
    userDetails = user,
    expirationDate = Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration),
  )

}