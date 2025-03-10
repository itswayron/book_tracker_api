package dev.wayron.book_tracker_api.modules.repositories.reading

import dev.wayron.book_tracker_api.modules.models.reading.ReadingLog
import org.springframework.data.jpa.repository.JpaRepository

interface ReadingLogRepository : JpaRepository<ReadingLog, Int>