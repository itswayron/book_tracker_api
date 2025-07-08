package dev.wayron.book_tracker_api.modules.services

import dev.wayron.book_tracker_api.modules.validators.Validator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.mockito.Mockito.*
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import javax.imageio.ImageIO
import kotlin.io.path.exists
import kotlin.io.path.writeBytes

class ImageServiceTest {

  @TempDir
  lateinit var tempDir: Path

  private lateinit var validator: Validator<MultipartFile>
  private lateinit var imageService: ImageService

  @BeforeEach
  fun setup() {
    validator = mock()
    imageService = ImageService(validator, tempDir)
  }

  @Test
  fun `saveImage saves image in covers folder when entityType is book`() {
    val fileContent = createValidJpgByteArray()
    val multipartFile = MockMultipartFile("file", "image.jpg", "image/jpeg", fileContent)

    doNothing().`when`(validator).validate(multipartFile)

    val path = imageService.saveImage("book", "123", multipartFile)

    assertTrue(path.startsWith("/images/covers/book_123_"))
    val savedFiles = tempDir.resolve("covers").toFile().listFiles()
    assertTrue(savedFiles != null && savedFiles.any { it.name.startsWith("book_123_") })
  }

  @Test
  fun `saveImage saves image in profiles folder when entityType is user`() {
    val fileContent = createValidJpgByteArray()
    val multipartFile = MockMultipartFile("file", "image.jpg", "image/jpeg", fileContent)

    doNothing().`when`(validator).validate(multipartFile)

    val path = imageService.saveImage("user", "abc", multipartFile)

    assertTrue(path.startsWith("/images/profiles/user_abc_"))
    val savedFiles = tempDir.resolve("profiles").toFile().listFiles()
    assertTrue(savedFiles != null && savedFiles.any { it.name.startsWith("user_abc_") })
  }

  @Test
  fun `saveImage throws exception for invalid entityType`() {
    val fileContent = createValidJpgByteArray()
    val multipartFile = MockMultipartFile("file", "image.jpg", "image/jpeg", fileContent)

    doNothing().`when`(validator).validate(multipartFile)

    val exception = Assertions.assertThrows(IllegalArgumentException::class.java) {
      imageService.saveImage("invalidType", "123", multipartFile)
    }
    Assertions.assertEquals("Unsupported entityType: invalidType", exception.message)
  }

  @Test
  fun `saveImage calls validator validate once`() {
    val fileContent = createValidJpgByteArray()
    val multipartFile = MockMultipartFile("file", "image.jpg", "image/jpeg", fileContent)

    doNothing().`when`(validator).validate(multipartFile)

    imageService.saveImage("book", "123", multipartFile)

    verify(validator, times(1)).validate(multipartFile)
  }

  @Test
  fun `saveImage generates unique and formatted filenames`() {
    val fileContent = createValidJpgByteArray()
    val multipartFile = MockMultipartFile("file", "image.jpg", "image/jpeg", fileContent)

    doNothing().`when`(validator).validate(multipartFile)

    val path1 = imageService.saveImage("book", "123", multipartFile)
    val path2 = imageService.saveImage("book", "123", multipartFile)

    assertTrue(path1.startsWith("/images/covers/book_123_") && path1.endsWith(".jpg"))
    assertTrue(path2.startsWith("/images/covers/book_123_") && path2.endsWith(".jpg"))
    Assertions.assertNotEquals(path1, path2)
  }

  @Test
  fun `deleteImage removes existing file from disk`() {
    val folder = tempDir.resolve("covers")
    Files.createDirectories(folder)
    val file = folder.resolve("toDelete.jpg")
    file.writeBytes("dummy".toByteArray())

    val publicPath = "/images/covers/toDelete.jpg"
    assertTrue(file.exists())

    imageService.deleteImage(publicPath)

    assertTrue(!file.exists(), "File should have been deleted.")
  }

  @Test
  fun `deleteImage does nothing if file does not exist`() {
    val publicPath = "/images/covers/nonexistent.jpg"
    Assertions.assertDoesNotThrow {
      imageService.deleteImage(publicPath)
    }
  }

  private fun createValidJpgByteArray(): ByteArray {
    val image = java.awt.image.BufferedImage(1, 1, java.awt.image.BufferedImage.TYPE_INT_RGB)
    val baos = java.io.ByteArrayOutputStream()
    ImageIO.write(image, "jpg", baos)
    return baos.toByteArray()
  }
}