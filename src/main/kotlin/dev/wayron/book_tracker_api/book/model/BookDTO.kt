package dev.wayron.book_tracker_api.book.model

data class BookDTO(
  val id: Int,
  val title: String,
  val author: String,
  val pages: Int,
  val chapters: Int?,
)