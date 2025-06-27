package dev.wayron.book_tracker_api.modules.models.user

import dev.wayron.book_tracker_api.utils.Base62UUIDGenerator
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Entity
@Table(name = "users")
data class User(
  @Id
  val id: String = Base62UUIDGenerator.generate(),

  val isActive: Boolean = true,
  val profileImage: String? = null,

  val email: String,

  @Column(name = "username")
  val usernameField: String,

  @Column(name = "password")
  val passwordField: String,

  @Enumerated(EnumType.STRING)
  val role: Role = Role.USER,

  val createdAt: LocalDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
  val updatedAt: LocalDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
): UserDetails {
  override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
    return mutableListOf(SimpleGrantedAuthority("ROLE_${role.name}"))
  }

  override fun getPassword() = passwordField

  override fun getUsername() = usernameField

  override fun isEnabled() = true
}