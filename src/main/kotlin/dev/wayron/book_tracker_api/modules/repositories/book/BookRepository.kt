package dev.wayron.book_tracker_api.modules.repositories.book

import dev.wayron.book_tracker_api.modules.models.book.Book
import org.springframework.data.jpa.repository.JpaRepository

interface BookRepository : JpaRepository<Book, Int>, BookRepositoryCustom
