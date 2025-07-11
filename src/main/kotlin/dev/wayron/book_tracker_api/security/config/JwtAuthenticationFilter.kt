package dev.wayron.book_tracker_api.security.config

import dev.wayron.book_tracker_api.security.services.TokenService
import dev.wayron.book_tracker_api.security.services.UserService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
  private val userService: UserService,
  private val tokenService: TokenService,
) : OncePerRequestFilter() {

  override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
    val jwtToken = extractTokenFromHeaderOrCookie(request)

    if(jwtToken == null) {
      filterChain.doFilter(request, response)
      return
    }

    val username = tokenService.extractUsername(jwtToken)
    if (username != null && SecurityContextHolder.getContext().authentication == null) {
      val foundUser = userService.loadUserByUsername(username)
      if (tokenService.isValid(jwtToken, foundUser)) {
        updateContext(foundUser, request)
      }
      filterChain.doFilter(request, response)
    }
  }

  private fun updateContext(foundUser: UserDetails, request: HttpServletRequest) {
    val authToken = UsernamePasswordAuthenticationToken(foundUser, null, foundUser.authorities)
    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
    SecurityContextHolder.getContext().authentication = authToken
  }

  private fun extractTokenFromHeaderOrCookie(request: HttpServletRequest): String? {
    val authHeader = request.getHeader("Authorization")
    if(!authHeader.doesNotContainBearerToken()) {
      return authHeader.extractTokenValue()
    }

    val tokenCookie = request.cookies?.find { it.name == "access_token" }
    return tokenCookie?.value
  }

  private fun String?.doesNotContainBearerToken(): Boolean = this == null || !this.startsWith("Bearer ")

  private fun String.extractTokenValue(): String = this.substringAfter("Bearer ")
}