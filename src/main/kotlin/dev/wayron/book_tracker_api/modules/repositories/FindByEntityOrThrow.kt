package dev.wayron.book_tracker_api.modules.repositories

import dev.wayron.book_tracker_api.modules.exceptions.ExceptionProvider
import jakarta.persistence.EntityNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.data.jpa.repository.JpaRepository

inline fun <reified T : Any, ID : Any> JpaRepository<T, ID>.findEntityByIdOrThrow(id: ID): T {
  val callerClassName = Throwable().stackTrace[1].className
  val logger = LoggerFactory.getLogger(Class.forName(callerClassName))
  val entityName = T::class.simpleName

  logger.debug("Searching for {} with ID: {}", entityName, id)
  return this.findById(id).orElseThrow {
    logger.error("$entityName with id: $id does not exist.")
    if (this is ExceptionProvider<*>) {
      @Suppress("UNCHECKED_CAST")
      (this as ExceptionProvider<ID>).notFoundException(id)
    } else {
      EntityNotFoundException("$entityName with id: $id does not exist.")
    }
  }.also {
    logger.debug("Successfully found {} with ID: {}", entityName, id)
  }
}
