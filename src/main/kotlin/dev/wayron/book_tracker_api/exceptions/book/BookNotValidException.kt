package dev.wayron.book_tracker_api.exceptions.book

import dev.wayron.book_tracker_api.exceptions.ExceptionErrorMessages

class BookNotValidException(val errors: List<String>) : IllegalArgumentException(ExceptionErrorMessages.BOOK_NOT_VALID.message)