package dev.wayron.book_tracker_api.modules.exceptions

import jakarta.persistence.EntityNotFoundException

interface ExceptionProvider<ID> {
  fun notFoundException(id: ID): EntityNotFoundException
}
