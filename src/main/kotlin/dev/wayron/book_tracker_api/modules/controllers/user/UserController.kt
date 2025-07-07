package dev.wayron.book_tracker_api.modules.controllers.user

import dev.wayron.book_tracker_api.config.ApiRoutes
import dev.wayron.book_tracker_api.modules.models.user.UserRequest
import dev.wayron.book_tracker_api.modules.models.user.UserResponse
import dev.wayron.book_tracker_api.modules.services.user.UploadUserProfileImageService
import dev.wayron.book_tracker_api.security.services.UserService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping(ApiRoutes.USER)
@SecurityRequirement(name = "bearerAuth")
class UserController(
  private val service: UserService,
  private val uploadUserProfileImageService: UploadUserProfileImageService
) {

  @PostMapping
  fun createUser(@RequestBody userRequest: UserRequest): ResponseEntity<UserResponse> {
    val userCreated = service.createUser(userRequest)
    return ResponseEntity(userCreated, HttpStatus.CREATED)
  }

  @GetMapping("/{id}")
  fun findUserById(@PathVariable id: String): ResponseEntity<UserResponse> {
    val response = service.findUserById(id)
    return ResponseEntity(response, HttpStatus.OK)
  }

  @PostMapping("/profile", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
  fun uploadProfileImage(@RequestPart("profileImage") profileImageFile: MultipartFile): ResponseEntity<Unit> {
    uploadUserProfileImageService.execute(profileImageFile)
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
  }
}
