package dev.wayron.book_tracker_api.book.model

import dev.wayron.book_tracker_api.reading.model.ReadingSession

data class BookDTO(
  val id: Int,
  val title: String,
  val author: String,
  val pages: Int,
  val chapters: Int?,
  val readingSession: ReadingSession,
) {
}