package dev.wayron.book_tracker_api.modules.services.user

import dev.wayron.book_tracker_api.modules.models.user.User
import dev.wayron.book_tracker_api.modules.repositories.user.UserRepository
import dev.wayron.book_tracker_api.modules.repositories.user.getCurrentUser
import dev.wayron.book_tracker_api.modules.services.ImageService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

class UploadUserProfileImageServiceTest {

  private lateinit var repository: UserRepository
  private lateinit var imageService: ImageService
  private lateinit var imageFile: MultipartFile
  private lateinit var service: UploadUserProfileImageService

  private lateinit var user: User

  @BeforeEach
  fun setUp() {
    repository = mock(UserRepository::class.java)
    imageService = mock(ImageService::class.java)
    imageFile = mock(MultipartFile::class.java)

    service = UploadUserProfileImageService(repository, imageService)

    user = User(
      id = "123",
      usernameField = "wayron",
      email = "wayron@example.com",
      passwordField = "hashed_password",
      profileImagePath = null,
      createdAt = LocalDateTime.now(),
      updatedAt = LocalDateTime.now()
    )

    val auth = mock(Authentication::class.java)
    `when`(auth.name).thenReturn("wayron")

    val context = mock(SecurityContext::class.java)
    `when`(context.authentication).thenReturn(auth)

    SecurityContextHolder.setContext(context)

    `when`(repository.findByUsernameField("wayron")).thenReturn(user)
  }

  @AfterEach
  fun tearDown() {
    SecurityContextHolder.clearContext()
  }

  @Test
  fun `should upload image when user has no previous image`() {
    `when`(repository.getCurrentUser()).thenReturn(user)
    `when`(imageService.saveImage("user", user.id, imageFile)).thenReturn("user/123/image.png")

    service.execute(imageFile)

    verify(imageService, never()).deleteImage(anyString())
    verify(imageService).saveImage("user", user.id, imageFile)
    verify(repository).save(user)

    assert(user.profileImagePath == "user/123/image.png")
  }

  @Test
  fun `should delete previous image before uploading new one`() {
    val previousPath = "user/123/old.png"
    user.profileImagePath = previousPath

    `when`(repository.getCurrentUser()).thenReturn(user)
    `when`(imageService.saveImage("user", user.id, imageFile)).thenReturn("user/123/new.png")

    service.execute(imageFile)

    verify(imageService).deleteImage(previousPath)
    verify(imageService).saveImage("user", user.id, imageFile)
    verify(repository).save(user)

    assert(user.profileImagePath == "user/123/new.png")
  }

  @Test
  fun `should log warning but continue if image deletion fails`() {
    val previousPath = "user/123/old.png"
    user.profileImagePath = previousPath

    `when`(repository.getCurrentUser()).thenReturn(user)
    `when`(imageService.deleteImage(previousPath)).thenThrow(RuntimeException("File not found"))
    `when`(imageService.saveImage("user", user.id, imageFile)).thenReturn("user/123/new.png")

    service.execute(imageFile)

    verify(imageService).deleteImage(previousPath)
    verify(imageService).saveImage("user", user.id, imageFile)
    verify(repository).save(user)

    assert(user.profileImagePath == "user/123/new.png")
  }
}
