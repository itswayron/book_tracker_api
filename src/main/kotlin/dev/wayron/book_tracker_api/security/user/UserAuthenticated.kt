package dev.wayron.book_tracker_api.security.user

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserAuthenticated(private val user: UserEntity) : UserDetails {

  override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
    return mutableListOf(SimpleGrantedAuthority("ROLE_${user.role.name}"))
  }

  override fun getPassword() = user.password

  override fun getUsername() = user.username

  override fun isEnabled() = user.isActive
}