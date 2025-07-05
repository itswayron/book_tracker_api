package dev.wayron.book_tracker_api.security.controllers

import dev.wayron.book_tracker_api.config.ApiRoutes
import dev.wayron.book_tracker_api.security.models.AuthenticationRequest
import dev.wayron.book_tracker_api.security.services.AuthenticationService
import jakarta.servlet.http.HttpServletResponse
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
    fun authenticate(@RequestBody authRequest: AuthenticationRequest, response: HttpServletResponse) {
        val token = authenticationService.authentication(authRequest)

        val cookie = jakarta.servlet.http.Cookie("access_token", token.accessToken).apply {
            isHttpOnly = true
            path = "/"
            maxAge = 60 * 60 * 100
            secure = false
        }

        response.addCookie(cookie)
        response.status = HttpServletResponse.SC_NO_CONTENT
    }
}