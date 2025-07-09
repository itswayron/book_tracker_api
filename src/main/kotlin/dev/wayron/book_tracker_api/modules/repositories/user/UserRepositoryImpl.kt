package dev.wayron.book_tracker_api.modules.repositories.user

import dev.wayron.book_tracker_api.modules.exceptions.user.UserNotFoundException
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl() : UserRepositoryCustom {
  override fun notFoundException(id: String): EntityNotFoundException = UserNotFoundException(id)
}
