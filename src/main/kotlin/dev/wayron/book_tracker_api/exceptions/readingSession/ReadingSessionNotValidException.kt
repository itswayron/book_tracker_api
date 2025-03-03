package dev.wayron.book_tracker_api.exceptions.readingSession

import dev.wayron.book_tracker_api.exceptions.ExceptionErrorMessages

class ReadingSessionNotValidException(val errors: List<String>) : IllegalArgumentException(ExceptionErrorMessages.READING_NOT_VALID.message)