package dev.wayron.book_tracker_api.modules.validations

fun interface Validator<T> {
  fun validate(t: T)
}