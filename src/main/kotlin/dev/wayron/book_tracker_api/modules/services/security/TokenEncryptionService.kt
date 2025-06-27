package dev.wayron.book_tracker_api.modules.services.security


import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Service
class TokenEncryptionService {
  private val secretKey: SecretKey = getSecretKeyFromEnv()

  private fun getSecretKeyFromEnv(): SecretKey {
    val secretKeyString = System.getenv("TOKEN_REFRESH_KEY")
    val sha512Digest = MessageDigest.getInstance("SHA-512")
    val hash = sha512Digest.digest(secretKeyString.toByteArray())
    val keyBytes = hash.copyOf(32)
    return SecretKeySpec(keyBytes, "AES")
  }

  fun encryptToken(token: String): String {
    val cipher = Cipher.getInstance("AES")
    cipher.init(Cipher.ENCRYPT_MODE, secretKey)
    val encryptedToken = cipher.doFinal(token.toByteArray())
    return Base64.getEncoder().encodeToString(encryptedToken)
  }

  fun decryptToken(encryptedToken: String): String {
    val cipher = Cipher.getInstance("AES")
    cipher.init(Cipher.DECRYPT_MODE, secretKey)
    val decodedToken = Base64.getDecoder().decode(encryptedToken)
    val decryptedToken = cipher.doFinal(decodedToken)
    return String(decryptedToken)
  }
}