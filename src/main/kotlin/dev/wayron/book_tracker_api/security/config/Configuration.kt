package dev.wayron.book_tracker_api.security.config

import dev.wayron.book_tracker_api.modules.models.user.UserRequest
import dev.wayron.book_tracker_api.modules.repositories.user.UserRepository
import dev.wayron.book_tracker_api.modules.validators.Validator
import dev.wayron.book_tracker_api.security.services.UserService
import dev.wayron.book_tracker_api.security.validators.UserPersistenceValidator
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
  fun userService(
    userRepository: UserRepository,
    encoder: PasswordEncoder,
    validator: Validator<UserRequest>,
    persistenceValidator: UserPersistenceValidator
  ) =
    UserService(userRepository, encoder, validator, persistenceValidator)

  @Bean
  fun encoder(): PasswordEncoder = BCryptPasswordEncoder()

  @Bean
  fun authenticationProvider(
    userService: UserService,
    encoder: PasswordEncoder
  ): AuthenticationProvider =
    DaoAuthenticationProvider(userService).apply {
      setPasswordEncoder(encoder)
    }

  @Bean
  fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager = config.authenticationManager
}
