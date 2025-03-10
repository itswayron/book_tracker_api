package dev.wayron.book_tracker_api.modules.repositories.reading

import dev.wayron.book_tracker_api.modules.models.reading.ReadingSession
import org.springframework.data.jpa.repository.JpaRepository

interface ReadingSessionRepository: JpaRepository<ReadingSession, Int> {
  fun findByBookId(bookId: Int) : List<ReadingSession>
}