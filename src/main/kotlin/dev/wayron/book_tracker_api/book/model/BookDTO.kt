package dev.wayron.book_tracker_api.book.model

import dev.wayron.book_tracker_api.reading.model.Reading

data class BookDTO(
  val id: Int,
  val title: String,
  val author: String,
  val pages: Int,
  val chapters: Int?,
  val reading: Reading,
) {
}