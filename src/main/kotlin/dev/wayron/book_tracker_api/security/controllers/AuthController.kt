package dev.wayron.book_tracker_api.security.controllers

import dev.wayron.book_tracker_api.config.ApiRoutes
import dev.wayron.book_tracker_api.security.models.AuthenticationRequest
import dev.wayron.book_tracker_api.security.services.AuthenticationService
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.math.max

@RestController
@RequestMapping(ApiRoutes.AUTH)
class AuthController(
    private val authenticationService: AuthenticationService
) {

    @PostMapping("/login")
    fun authenticate(@RequestBody authRequest: AuthenticationRequest, response: HttpServletResponse) {
        val token = authenticationService.authentication(authRequest)

        val cookie = Cookie("access_token", token.accessToken).apply {
            isHttpOnly = true
            path = "/"
            secure = false
            maxAge = 60 * 60 * 100
        }

        response.addCookie(cookie)
        response.status = HttpServletResponse.SC_NO_CONTENT
    }

    @PostMapping("/logout")
    fun logout(response: HttpServletResponse) {
        val expiredCookie = Cookie("access_token", "").apply {
            isHttpOnly = true
            path = "/"
            secure = false
            maxAge = 0
        }

        response.addCookie(expiredCookie)
        response.status = HttpServletResponse.SC_NO_CONTENT
    }
}