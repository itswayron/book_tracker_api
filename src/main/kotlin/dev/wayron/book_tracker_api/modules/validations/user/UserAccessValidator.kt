package dev.wayron.book_tracker_api.modules.validations.user

import dev.wayron.book_tracker_api.security.user.Role

interface UserAccessValidator<T, E> {
  fun validateUserAccess(t: T, e : E, userRole: Role)
}