package dev.wayron.book_tracker_api.entities.book.repositories

import dev.wayron.book_tracker_api.entities.book.model.Book
import org.springframework.data.jpa.repository.JpaRepository

interface BookRepository : JpaRepository<Book, Int>