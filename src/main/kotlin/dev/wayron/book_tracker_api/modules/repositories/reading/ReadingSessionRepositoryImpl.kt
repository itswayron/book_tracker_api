package dev.wayron.book_tracker_api.modules.repositories.reading

import dev.wayron.book_tracker_api.modules.exceptions.reading.ReadingSessionNotFoundException
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Repository

@Repository
class ReadingSessionRepositoryImpl : ReadingSessionRepositoryCustom {
  override fun notFoundException(id: Int): EntityNotFoundException = ReadingSessionNotFoundException(id)
}
