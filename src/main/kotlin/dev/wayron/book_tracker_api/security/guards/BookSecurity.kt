package dev.wayron.book_tracker_api.security.guards

import dev.wayron.book_tracker_api.modules.repositories.book.BookRepository
import dev.wayron.book_tracker_api.modules.repositories.user.UserRepository
import dev.wayron.book_tracker_api.modules.repositories.user.getCurrentUser
import org.springframework.stereotype.Component

@Component("bookSecurity")
class BookSecurity(
  private val bookRepository: BookRepository,
  private val userRepository: UserRepository,
) {

  fun isBookOwner(bookId: Int): Boolean {
    val user = userRepository.getCurrentUser()
    val book = bookRepository.findById(bookId)
    return book.isPresent && book.get().user.id == user.id
  }
}
