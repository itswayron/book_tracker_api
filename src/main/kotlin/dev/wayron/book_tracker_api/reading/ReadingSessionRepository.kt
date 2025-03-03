package dev.wayron.book_tracker_api.reading

import dev.wayron.book_tracker_api.reading.model.ReadingSession
import org.springframework.data.jpa.repository.JpaRepository

interface ReadingSessionRepository: JpaRepository<ReadingSession, Int> {
  fun findByBookId(bookId: Int) : List<ReadingSession>
}