package dev.wayron.book_tracker_api.security.controller

import dev.wayron.book_tracker_api.security.service.UserService
import dev.wayron.book_tracker_api.security.user.UserRequest
import dev.wayron.book_tracker_api.security.user.UserResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
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