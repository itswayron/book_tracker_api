package dev.wayron.book_tracker_api.security.service

import dev.wayron.book_tracker_api.security.repository.UserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService

typealias ApplicationUser = dev.wayron.book_tracker_api.security.user.UserEntity

class CustomUserDetailsService(private val repository: UserRepository) : UserDetailsService {
  override fun loadUserByUsername(username: String): UserDetails {
    val foundUser = repository.findByUsername(username).orElseThrow()
    return foundUser.mapToUserDetails()
  }

  private fun ApplicationUser.mapToUserDetails(): UserDetails =
    User.builder()
      .username(this.username)
      .password(this.password)
      .roles(this.role.name)
      .build()
}