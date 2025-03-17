package dev.wayron.book_tracker_api.security.config


import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfiguration(
  private val authenticationProvider: AuthenticationProvider
) {

  @Bean
  fun securityFilterChain(
    http: HttpSecurity,
    jwtAuthenticationFilter: JwtAuthenticationFilter
  ): DefaultSecurityFilterChain =
    http.csrf { it.disable() }
      .authorizeHttpRequests {
        it.requestMatchers("/login**", "/login/**", "/user/**", "/error").permitAll()
          .requestMatchers(HttpMethod.GET, "/books/**", "/readings/**").permitAll()
          .requestMatchers(HttpMethod.POST, "/books/**", "/readings/**").authenticated()
          .requestMatchers(HttpMethod.PUT, "/books/**").authenticated()
          .requestMatchers(HttpMethod.DELETE, "/books/**").authenticated()
          .anyRequest().fullyAuthenticated()
      }
      .sessionManagement {
        it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      }
      .authenticationProvider(authenticationProvider)
      .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
      .build()
}