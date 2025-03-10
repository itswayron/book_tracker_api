package dev.wayron.book_tracker_api.modules.exceptions

enum class ExceptionErrorMessages(val message: String) {
  BOOK_NOT_FOUND("Book not found."),
  BOOK_NOT_VALID("Book is not valid."),
  READING_NOT_FOUND("Reading not found."),
  READING_NOT_VALID("Reading is not valid"),
  READING_ALREADY_FINISHED("Cannot add logs to a finished reading session."),
  LOG_WITH_INVALID_VALUE("The amount of reading must be positive."),
}