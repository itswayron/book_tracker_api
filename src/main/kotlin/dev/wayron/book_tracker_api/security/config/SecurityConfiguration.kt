package dev.wayron.book_tracker_api.security.config

import dev.wayron.book_tracker_api.security.user.Role
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
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
        it.requestMatchers(HttpMethod.GET, "/books/**", "/readings/**").permitAll()
        it.requestMatchers(HttpMethod.POST, "/books/**", "/readings/**").authenticated()
        it.requestMatchers(HttpMethod.PUT, "/books/**").authenticated()
        it.requestMatchers(HttpMethod.DELETE, "/books/**").authenticated()
        it.requestMatchers("/**").hasRole(Role.ADMIN.name)
        it.anyRequest().fullyAuthenticated()
      }
      .sessionManagement {
        it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      }
      .authenticationProvider(authenticationProvider)
      .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
      .build()
}