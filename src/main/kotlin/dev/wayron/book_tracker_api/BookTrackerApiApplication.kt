package dev.wayron.book_tracker_api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BookTrackerApiApplication

fun main(args: Array<String>) {
	runApplication<BookTrackerApiApplication>(*args)
}
