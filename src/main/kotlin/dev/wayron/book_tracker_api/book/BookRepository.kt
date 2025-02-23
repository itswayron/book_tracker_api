package dev.wayron.book_tracker_api.book

import dev.wayron.book_tracker_api.book.model.Book
import org.springframework.data.jpa.repository.JpaRepository

interface BookRepository : JpaRepository<Book, Int>