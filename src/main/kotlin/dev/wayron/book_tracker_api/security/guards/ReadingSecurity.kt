package dev.wayron.book_tracker_api.security.guards

import dev.wayron.book_tracker_api.modules.repositories.UserRepository
import dev.wayron.book_tracker_api.modules.repositories.reading.ReadingSessionRepository
import dev.wayron.book_tracker_api.utils.findEntityByIdOrThrow
import dev.wayron.book_tracker_api.utils.getCurrentUser
import org.springframework.stereotype.Component

@Component("readingSecurity")
class ReadingSecurity(
  private val sessionRepository: ReadingSessionRepository,
  private val userRepository: UserRepository
) {

  fun isOwner(sessionId: Int): Boolean {
    val session = sessionRepository.findEntityByIdOrThrow(sessionId)
    val currentUser = userRepository.getCurrentUser()
    return session.userId.id == currentUser.id
  }

}