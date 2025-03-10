package dev.wayron.book_tracker_api.utils

enum class ValidationErrorMessages(val message: String) {
  EMPTY_TITLE("Title cannot be empty."),
  EMPTY_AUTHOR("Author cannot be empty."),
  PAGES_NOT_POSITIVE("Pages must be greater than zero."),
  NEGATIVE_CHAPTERS("Chapters cannot be negative."),
  BOOK_HAS_NO_CHAPTERS("Cannot track by chapters if the book has no chapters."),
  NEGATIVE_DAILY_GOAL("Cannot have a negative daily goal."),
  FUTURE_START_READING("Cannot start reading a book in the future."),
  FUTURE_END_READING("Cannot finish a book on the future."),
}