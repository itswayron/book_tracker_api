package dev.wayron.book_tracker_api.utils

import jakarta.persistence.EntityNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.data.jpa.repository.JpaRepository

inline fun <reified T : Any> JpaRepository<T, Int>.findEntityByIdOrThrow(id: Int): T {
  val callerClassName = Throwable().stackTrace[1].className
  val logger = LoggerFactory.getLogger(Class.forName(callerClassName))

  logger.debug("Searching for ${T::class.simpleName} with ID: $id")
  return this.findById(id).orElseThrow {
    logger.error("${T::class.simpleName} with id: $id does not exist.")
    EntityNotFoundException("${T::class.simpleName} with id: $id does not exist.")
  }.also {
    logger.debug("Successfully found ${T::class.simpleName} with ID: $id")
  }
}
