package dev.wayron.book_tracker_api.modules.validators.image

import dev.wayron.book_tracker_api.modules.exceptions.ImageNotValidException
import dev.wayron.book_tracker_api.modules.validators.ValidationErrorMessages
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.springframework.mock.web.MockMultipartFile
import javax.imageio.ImageIO
import kotlin.test.Test

class ImageValidatorTest {

  private lateinit var validator: ImageValidator

  @BeforeEach
  fun setup() {
    validator = ImageValidator()
  }

  @Test
  fun `should throw when file is empty`() {
    val emptyFile = MockMultipartFile("file", "image.jpg", "image/jpeg", ByteArray(0))

    val exception = assertThrows(ImageNotValidException::class.java) {
      validator.validate(emptyFile)
    }

    assertTrue(exception.errors.contains(ValidationErrorMessages.IMAGE_EMPTY.message))
  }

  @Test
  fun `should throw when file is not an image`() {
    val file = MockMultipartFile("file", "text.txt", "text/plain", "Not an image".toByteArray())

    val exception = assertThrows(ImageNotValidException::class.java) {
      validator.validate(file)
    }

    assertTrue(exception.errors.contains(ValidationErrorMessages.NOT_IMAGE.message))
  }

  @Test
  fun `should throw when file is too big`() {
    val content = ByteArray(6 * 1024 * 1024) { 1 }
    val file = MockMultipartFile("file", "image.jpg", "image/jpeg", content)

    val exception = assertThrows(ImageNotValidException::class.java) {
      validator.validate(file)
    }

    assertTrue(exception.errors.contains(ValidationErrorMessages.BIG_FILE.message))
  }

  @Test
  fun `should throw when image is too small`() {
    val image = java.awt.image.BufferedImage(100, 100, java.awt.image.BufferedImage.TYPE_INT_RGB)
    val baos = java.io.ByteArrayOutputStream()
    ImageIO.write(image, "jpg", baos)
    val content = baos.toByteArray()

    val file = MockMultipartFile("file", "small.jpg", "image/jpeg", content)

    val exception = assertThrows(ImageNotValidException::class.java) {
      validator.validate(file)
    }

    assertTrue(exception.errors.contains(ValidationErrorMessages.SMALL_DIMENSIONS.message))
  }

  @Test
  fun `should throw when image is not readable by ImageIO`() {
    val file = MockMultipartFile("file", "corrupted.jpg", "image/jpeg", "notReallyAnImage".toByteArray())

    val exception = assertThrows(ImageNotValidException::class.java) {
      validator.validate(file)
    }

    assertTrue(exception.errors.contains(ValidationErrorMessages.UNSUPPORTED_IMAGE.message))
  }

  @Test
  fun `should pass with a valid image`() {
    val image = java.awt.image.BufferedImage(300, 300, java.awt.image.BufferedImage.TYPE_INT_RGB)
    val baos = java.io.ByteArrayOutputStream()
    ImageIO.write(image, "jpg", baos)
    val content = baos.toByteArray()

    val file = MockMultipartFile("file", "valid.jpg", "image/jpeg", content)

    assertDoesNotThrow {
      validator.validate(file)
    }
  }
}
