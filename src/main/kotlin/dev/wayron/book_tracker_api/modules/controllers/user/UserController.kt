package dev.wayron.book_tracker_api.modules.controllers.user

import dev.wayron.book_tracker_api.modules.services.security.UserService
import dev.wayron.book_tracker_api.modules.models.user.UserRequest
import dev.wayron.book_tracker_api.modules.models.user.UserResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
@SecurityRequirement(name = "bearerAuth")
class UserController(private val service: UserService) {

  @PostMapping
  fun createUser(@RequestBody userRequest: UserRequest): ResponseEntity<UserResponse> {
    val userCreated = service.createUser(userRequest)
    return ResponseEntity.status(HttpStatus.CREATED).body(userCreated)
  }

  @GetMapping("/{id}")
  fun findUserById(@PathVariable id: String): ResponseEntity<UserResponse> {
    val user = service.findUserById(id)
    return ResponseEntity.status(HttpStatus.OK).body(user)
  }

}