package dev.wayron.book_tracker_api.book.model

data class UpdateProductCommand(val id: Int, val book: Book)