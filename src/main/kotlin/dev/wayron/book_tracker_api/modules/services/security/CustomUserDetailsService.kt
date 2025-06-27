package dev.wayron.book_tracker_api.modules.services.security

import dev.wayron.book_tracker_api.modules.repositories.UserRepository
import dev.wayron.book_tracker_api.modules.models.user.UserEntity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService

typealias ApplicationUser = UserEntity

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