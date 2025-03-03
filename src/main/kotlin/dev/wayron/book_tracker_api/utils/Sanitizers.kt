package dev.wayron.book_tracker_api.utils

import dev.wayron.book_tracker_api.entities.book.model.Book

object Sanitizers {
  fun sanitizeBook(book: Book): Book {
    return book.copy(
      publisher = book.publisher?.takeIf { it.isNotBlank() },
      language = book.language?.takeIf { it.isNotBlank() },
      typeOfMedia = book.typeOfMedia?.takeIf { it.isNotBlank() },
      isbn10 = book.isbn10?.takeIf { it.isNotBlank() },
      isbn13 = book.isbn13?.takeIf { it.isNotBlank() }
    )
  }
}