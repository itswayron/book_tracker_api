package dev.wayron.book_tracker_api.modules.validators

fun interface Validator<T> {
  fun validate(t: T)
}