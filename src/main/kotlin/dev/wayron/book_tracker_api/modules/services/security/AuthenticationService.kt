package dev.wayron.book_tracker_api.modules.services.security

import dev.wayron.book_tracker_api.modules.config.security.JwtProperties
import dev.wayron.book_tracker_api.modules.models.security.AuthenticationRequest
import dev.wayron.book_tracker_api.modules.models.security.AuthenticationResponse
import dev.wayron.book_tracker_api.modules.models.security.RefreshTokenEntity
import dev.wayron.book_tracker_api.modules.repositories.RefreshTokenRepository
import dev.wayron.book_tracker_api.modules.repositories.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthenticationService(
  private val authManager: AuthenticationManager,
  private val userService: UserService,
  private val userDetailsService: CustomUserDetailsService,
  private val tokenService: TokenService,
  private val jwtProperties: JwtProperties,
  private val refreshTokenRepository: RefreshTokenRepository,
  private val tokenEncryptionService: TokenEncryptionService,
  private val userRepository: UserRepository,
) {

  fun authentication(authRequest: AuthenticationRequest): AuthenticationResponse {
    authManager.authenticate(
      UsernamePasswordAuthenticationToken(
        authRequest.username, authRequest.password
      )
    )
    val user = userDetailsService.loadUserByUsername(authRequest.username)
    val accessToken = generateAccessToken(user)
    val refreshToken = generateRefreshToken(user)
    val encryptedRefreshToken = tokenEncryptionService.encryptToken(refreshToken.token)
    refreshTokenRepository.save(refreshToken.copy(token = encryptedRefreshToken))
    return AuthenticationResponse(accessToken, refreshToken = refreshToken.token)
  }

  private fun generateRefreshToken(user: UserDetails): RefreshTokenEntity {
    val issuedAt = Date(System.currentTimeMillis())
    val expiredAt = Date(System.currentTimeMillis() + jwtProperties.refreshTokenExpiration)
    val userEntity = userRepository.findByUsername(user.username).orElseThrow()
    return RefreshTokenEntity(
      token = tokenService.generate(user, expiredAt),
      user = userEntity,
      issuedAt = issuedAt,
      expiredAt = expiredAt,
    )
  }

  private fun generateAccessToken(user: UserDetails) = tokenService.generate(
    userDetails = user,
    expirationDate = Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration),
  )

  fun refreshAccessToken(token: String): String? {
    val encryptedToken = tokenEncryptionService.encryptToken(token)
    val extractedUsername = tokenService.extractUsername(token) ?: throw IllegalArgumentException("Failed to extract username from token.")
    val currentUserEntity = userRepository.findByUsername(extractedUsername).orElseThrow()
    val currentUserDetails = userDetailsService.loadUserByUsername(extractedUsername)

    val refreshTokenUserDetail = refreshTokenRepository.findByToken(encryptedToken)
    val isTokenValid = !tokenService.isExpired(token) && currentUserEntity.username == refreshTokenUserDetail?.user?.username

    return if (isTokenValid) {
      generateAccessToken(currentUserDetails)
    } else {
      null
    }

  }

}