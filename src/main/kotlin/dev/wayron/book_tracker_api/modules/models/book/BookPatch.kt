package dev.wayron.book_tracker_api.modules.models.book

import java.time.LocalDate

data class BookPatch(
  val title: String? = null,
  val author: String? = null,
  val pages: Int? = null,
  val chapters: Int? = null,

  val synopsis: String? = null,
  val publisher: String? = null,
  val publicationDate: LocalDate? = null,
  val language: String? = null,
  val isbn10: String? = null,
  val isbn13: String? = null,
  val typeOfMedia: String? = null,

  val genres: Set<String>? = emptySet()
)