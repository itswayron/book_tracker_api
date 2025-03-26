package dev.wayron.book_tracker_api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.web.config.EnableSpringDataWebSupport
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
class BookTrackerApiApplication

fun main(args: Array<String>) {
	runApplication<BookTrackerApiApplication>(*args)
}
