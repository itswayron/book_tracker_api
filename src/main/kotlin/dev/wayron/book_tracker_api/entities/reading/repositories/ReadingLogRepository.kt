package dev.wayron.book_tracker_api.entities.reading.repositories

import dev.wayron.book_tracker_api.entities.reading.model.ReadingLog
import org.springframework.data.jpa.repository.JpaRepository

interface ReadingLogRepository : JpaRepository<ReadingLog, Int>