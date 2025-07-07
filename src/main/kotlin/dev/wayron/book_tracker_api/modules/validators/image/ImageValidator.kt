package dev.wayron.book_tracker_api.modules.validators.image

import dev.wayron.book_tracker_api.modules.exceptions.ImageNotValidException
import dev.wayron.book_tracker_api.modules.validators.ValidationErrorMessages
import dev.wayron.book_tracker_api.modules.validators.Validator
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import javax.imageio.ImageIO

@Component
class ImageValidator : Validator<MultipartFile> {
  private val logger = LoggerFactory.getLogger(this::class.java)
  private val maxSizeBytes = 5 * 1024 * 1024

  init {
    ImageIO.scanForPlugins()
  }

  override fun validate(t: MultipartFile) {
    logger.debug("Validating image: ${t.name}")
    val errors = mutableListOf<String>()
    validateFileExists(t, errors)
    validateIsImage(t, errors)
    validateFileSize(t, errors)
    validateImageDimensions(t, errors)

    if (errors.isNotEmpty()) {
      logger.error("Image is not valid")
      throw ImageNotValidException(errors)
    }

    logger.debug("Valid image.")
  }

  private fun validateFileExists(file: MultipartFile, errors: MutableList<String>) {
    logger.debug("Validating if files exists.")
    if (file.isEmpty) {
      logger.error("File does not exists.")
      errors.add(ValidationErrorMessages.IMAGE_EMPTY.message)
    } else {
      logger.debug("File exists.")
    }
  }

  private fun validateIsImage(file: MultipartFile, errors: MutableList<String>) {
    logger.debug("Validating if file is image.")
    if (file.contentType?.startsWith("image/") != true) {
      logger.error("File is not an image.")
      errors.add(ValidationErrorMessages.NOT_IMAGE.message)
    } else {
      logger.debug("Valid file type.")
    }
  }

  private fun validateFileSize(file: MultipartFile, errors: MutableList<String>) {
    logger.debug("Validating file size.")
    if (file.size > maxSizeBytes) {
      logger.error("File is too big")
      errors.add(ValidationErrorMessages.BIG_FILE.message)
    } else {
      logger.debug("Valid file size.")
    }
  }

  private fun validateImageDimensions(file: MultipartFile, errors: MutableList<String>) {
    logger.debug("Validating image dimensions")
    val image = ImageIO.read(file.inputStream)
    if (image == null) {
      logger.error("Unsupported image format or unable to read image.")
      errors.add(ValidationErrorMessages.UNSUPPORTED_IMAGE.message)
      return
    }
    if (image.width < 200 || image.height < 200) {
      logger.error("Image dimensions are too small.")
      errors.add(ValidationErrorMessages.SMALL_DIMENSIONS.message)
    } else {
      logger.debug("Valid image dimensions.")
    }
  }
}
