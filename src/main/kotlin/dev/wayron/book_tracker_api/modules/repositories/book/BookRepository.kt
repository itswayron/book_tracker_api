package dev.wayron.book_tracker_api.modules.repositories.book

import dev.wayron.book_tracker_api.modules.models.book.Book
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface BookRepository : JpaRepository<Book, Int>, BookRepositoryCustom {

  @Query(
    """
    SELECT b from Book b WHERE b.visibility = 'PUBLIC'
      OR (b.visibility = 'PRIVATE' AND b.user.id = :userId)
  """
  )
  fun findVisibleBooksForUser(userId: String, pageable: Pageable): Page<Book>
}
