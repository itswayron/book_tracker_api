package dev.wayron.book_tracker_api.modules.services

import org.slf4j.LoggerFactory
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class EmailService(private val javaMailSender: JavaMailSender) {
  private val logger = LoggerFactory.getLogger(this::class.java)

  @Async
  fun sendGenericEmail(email: String, title: String, htmlMessage: String) {
    logger.info("Sending email to {}", email)

    val mimeMessage = javaMailSender.createMimeMessage()
    val helper = MimeMessageHelper(mimeMessage, "utf-8")

    helper.setTo(email)
    helper.setSubject("[Book Tracker] $title")
    helper.setText(htmlMessage)
    logger.debug("The subject is {} and the message is: {}.", helper.mimeMessage.subject, helper.mimeMessage.content)

    javaMailSender.send(mimeMessage)
  }

  @Async
  fun sendPasswordResetEmail(email: String, token: String) {
    logger.info("Sending reset password email to: '{}'", email)

    val mimeMessage = javaMailSender.createMimeMessage()
    val helper = MimeMessageHelper(mimeMessage, "utf-8")

    helper.setTo(email)
    helper.setSubject("[Book Tracker] Redefinição de senha")
    helper.setText("Para redefinir a sua senha, acesse:<br>http://localhost.com:8080/reset-password?token=$token<br><br>Token: $token<br>", true)
    logger.debug("Reset password email sent to {}", email)
    javaMailSender.send(mimeMessage)
  }
}
