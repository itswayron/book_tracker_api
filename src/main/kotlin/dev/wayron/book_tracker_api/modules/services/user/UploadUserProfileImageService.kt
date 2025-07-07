package dev.wayron.book_tracker_api.modules.services.user

import dev.wayron.book_tracker_api.modules.models.user.User
import dev.wayron.book_tracker_api.modules.repositories.user.UserRepository
import dev.wayron.book_tracker_api.modules.repositories.user.getCurrentUser
import dev.wayron.book_tracker_api.modules.services.ImageService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class UploadUserProfileImageService(
  private val repository: UserRepository,
  private val imageService: ImageService,
) {
  private val logger = LoggerFactory.getLogger(this::class.java)

  fun execute(imageFile: MultipartFile) {
    val user = repository.getCurrentUser()
    logger.info("Uploading profile image for user: ${user.username}")

    user.deleteImageIfExists()
    val imagePath = imageService.saveImage("user", user.id, imageFile)
    logger.debug("Saving new profile image for userId='{}'.", user.id)

    user.profileImagePath = imagePath
    user.updatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)

    repository.save(user)
    logger.info("Profile image updated successfully for user='{}'. New path='{}'.", user.username, imagePath)
  }

  private fun User.deleteImageIfExists() {
    this.profileImagePath?.let { path ->
      try {
        logger.debug("Attempting to delete existing profile image at path='{}'.", path)
        imageService.deleteImage(path)
        logger.info("Previous profile image deleted for user='{}'.", this.username)
      } catch (ex: Exception) {
        logger.warn(
          "Could not delete old profile image for user='{}'. Path='{}'. Reason: {}",
          this.username,
          path,
          ex.message
        )
      }
    } ?: logger.debug("No existing profile image to delete for user='{}'.", this.username)
  }
}
