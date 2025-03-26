package dev.wayron.book_tracker_api.modules.models.mappers

import dev.wayron.book_tracker_api.modules.models.reading.ReadingLog
import dev.wayron.book_tracker_api.modules.models.reading.ReadingSession
import dev.wayron.book_tracker_api.modules.models.reading.dto.ReadingLogResponse
import dev.wayron.book_tracker_api.modules.models.reading.dto.ReadingSessionResponse
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingConstants

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface ReadingMapper {

  @Mapping(source = "entity.readingSession.id", target = "readingSessionId")
  @Mapping(source = "entity.readingSession.book.id", target = "bookId")
  fun logEntityToResponse(entity: ReadingLog): ReadingLogResponse

  @Mapping(source = "entity.book.id", target = "bookId")
  @Mapping(source = "entity.book.title", target = "bookTitle")
  fun sessionEntityToResponse(entity: ReadingSession): ReadingSessionResponse
}