package dev.wayron.book_tracker_api.modules.exceptions

enum class ExceptionErrorMessages(val message: String, val details: String? = null) {
  BOOK_NOT_FOUND("Book not found."),
  BOOK_NOT_VALID("Book is not valid."),
  READING_NOT_FOUND("Reading not found."),
  READING_NOT_VALID("Reading is not valid"),
  READING_ALREADY_FINISHED("Cannot add logs to a finished reading session."),
  LOG_WITH_INVALID_VALUE("The amount of reading must be positive."),
  FORBIDDEN_ACCESS("The user don't have permission to perform this action."),
  INVALID_IMAGE("The image sent is not valid."),
  USER_NOT_FOUND("User not found."),
  USER_NOT_VALID("User is not valid."),
}
