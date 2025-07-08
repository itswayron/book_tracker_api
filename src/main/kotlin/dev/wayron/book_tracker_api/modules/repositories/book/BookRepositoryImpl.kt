package dev.wayron.book_tracker_api.modules.repositories.book

import dev.wayron.book_tracker_api.modules.exceptions.book.BookNotFoundException
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Repository

@Repository
class BookRepositoryImpl() : BookRepositoryCustom {
  override fun notFoundException(id: Int): EntityNotFoundException = BookNotFoundException(id)
}
