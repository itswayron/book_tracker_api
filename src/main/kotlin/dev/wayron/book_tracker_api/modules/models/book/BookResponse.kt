package dev.wayron.book_tracker_api.modules.models.book

data class BookResponse(
  val id: Int,
  val title: String,
  val author: String,
  val pages: Int,
  val chapters: Int?,
)