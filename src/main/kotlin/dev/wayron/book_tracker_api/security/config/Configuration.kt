package dev.wayron.book_tracker_api.security.config

import dev.wayron.book_tracker_api.modules.repositories.UserRepository
import dev.wayron.book_tracker_api.security.services.UserService
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
class Configuration {

  @Bean
  fun userService(userRepository: UserRepository) = UserService(userRepository, encoder())

  @Bean
  fun encoder(): PasswordEncoder = BCryptPasswordEncoder()

  @Bean
  fun authenticationProvider(userRepository: UserRepository): AuthenticationProvider =
    DaoAuthenticationProvider().also {
      it.setUserDetailsService(userService(userRepository))
      it.setPasswordEncoder(encoder())
    }

  @Bean
  fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager = config.authenticationManager

}