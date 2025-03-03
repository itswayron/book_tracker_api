package dev.wayron.book_tracker_api.utils

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {
  @Bean
  fun webClient(): WebClient {
    return WebClient.builder()
      .baseUrl("http://localhost:8080")
      .build()
  }
}