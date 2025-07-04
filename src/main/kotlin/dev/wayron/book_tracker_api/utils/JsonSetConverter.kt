package dev.wayron.book_tracker_api.utils

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class JsonSetConverter : AttributeConverter<Set<String>?, String> {

  private val objectMapper = jacksonObjectMapper()

  override fun convertToDatabaseColumn(attribute: Set<String>?): String? {
    return attribute?.let { objectMapper.writeValueAsString(it) }
  }

  override fun convertToEntityAttribute(dbData: String?): Set<String>? {
    return dbData?.let { objectMapper.readValue(it, Array<String>::class.java).toSet() }
  }
}