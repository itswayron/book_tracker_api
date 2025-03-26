package dev.wayron.book_tracker_api.modules.models.mappers

import dev.wayron.book_tracker_api.modules.models.book.*
import dev.wayron.book_tracker_api.modules.models.reading.*
import dev.wayron.book_tracker_api.modules.models.reading.dto.*
import dev.wayron.book_tracker_api.modules.models.reading.enums.*
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingConstants

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface BookMapper {

  fun entityBookToResponse(entity: Book): BookResponse
}