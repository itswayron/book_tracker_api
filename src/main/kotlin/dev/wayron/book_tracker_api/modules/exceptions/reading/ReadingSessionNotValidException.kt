package dev.wayron.book_tracker_api.modules.exceptions.reading

import dev.wayron.book_tracker_api.modules.exceptions.ExceptionErrorMessages

class ReadingSessionNotValidException(val errors: List<String>) : IllegalArgumentException(ExceptionErrorMessages.READING_NOT_VALID.message)