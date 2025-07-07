package dev.wayron.book_tracker_api.modules.models.mappers

import dev.wayron.book_tracker_api.modules.models.book.Book
import dev.wayron.book_tracker_api.modules.models.book.BookPatch
import dev.wayron.book_tracker_api.modules.models.book.BookRequest
import dev.wayron.book_tracker_api.modules.models.book.BookResponse
import dev.wayron.book_tracker_api.modules.models.user.User
import java.sql.Timestamp

fun BookRequest.toEntity(user: User): Book = Book(
  id = 0,
  title = this.title,
  author = this.author,
  pages = this.pages,
  chapters = this.chapters,
  userId = user,
  synopsis = this.synopsis,
  publisher = this.publisher,
  publicationDate = this.publicationDate,
  language = this.language,
  isbn10 = this.isbn10,
  isbn13 = this.isbn13,
  typeOfMedia = this.typeOfMedia,
  genres = this.genres,
)

fun Book.toResponse(): BookResponse = BookResponse(
  id = this.id,
  title = this.title,
  author = this.author,
  pages = this.pages,
  chapters = this.chapters,
  coverPath = toPublicUrl(this.coverPath),
)

fun Book.updateWith(request: BookPatch): Book {
  return this.copy(
    title = request.title ?: this.title,
    author = request.author ?: this.author,
    pages = request.pages ?: this.pages,
    chapters = request.chapters ?: this.chapters,
    synopsis = request.synopsis ?: this.synopsis,
    publisher = request.publisher ?: this.publisher,
    publicationDate = request.publicationDate ?: this.publicationDate,
    language = request.language ?: this.language,
    isbn10 = request.isbn10 ?: this.isbn10,
    isbn13 = request.isbn13 ?: this.isbn13,
    typeOfMedia = request.typeOfMedia ?: this.typeOfMedia,
    genres = request.genres ?: this.genres,
    updatedAt = Timestamp(System.currentTimeMillis())
  )
}

private fun toPublicUrl(path: String?): String? {
  return path
    ?.replace("\\", "/")
    ?.substringAfter("uploads/images")
    ?.removePrefix("/")
    ?.removePrefix("images/")
    ?.let { "/images/$it" }
}
