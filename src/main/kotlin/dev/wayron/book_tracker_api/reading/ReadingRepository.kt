package dev.wayron.book_tracker_api.reading

import dev.wayron.book_tracker_api.reading.model.Reading
import org.springframework.data.jpa.repository.JpaRepository

interface ReadingRepository: JpaRepository<Reading, Int>