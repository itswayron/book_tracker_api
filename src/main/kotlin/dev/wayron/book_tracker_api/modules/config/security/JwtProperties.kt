package dev.wayron.book_tracker_api.modules.config.security

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("jwt")
data class JwtProperties(
  val key: String,
  val accessTokenExpiration: Long,
)
