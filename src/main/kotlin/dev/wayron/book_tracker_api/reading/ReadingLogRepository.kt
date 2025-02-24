package dev.wayron.book_tracker_api.reading

import dev.wayron.book_tracker_api.reading.model.ReadingLog
import org.springframework.data.jpa.repository.JpaRepository

interface ReadingLogRepository : JpaRepository<ReadingLog, Int>