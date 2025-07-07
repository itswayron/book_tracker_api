package dev.wayron.book_tracker_api.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.nio.file.Paths

@Configuration
class StaticResourceConfig : WebMvcConfigurer {
  override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
    val uploadDir = Paths.get("uploads/images").toAbsolutePath().toUri().toString()
    registry.addResourceHandler("/images/**").addResourceLocations(uploadDir)
  }
}
