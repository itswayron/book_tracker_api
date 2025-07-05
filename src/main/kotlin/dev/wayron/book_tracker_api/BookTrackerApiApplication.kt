package dev.wayron.book_tracker_api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.web.config.EnableSpringDataWebSupport
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity

@SpringBootApplication
@EnableMethodSecurity(prePostEnabled = true)
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
class BookTrackerApiApplication

fun main(args: Array<String>) {
	runApplication<BookTrackerApiApplication>(*args)
}
