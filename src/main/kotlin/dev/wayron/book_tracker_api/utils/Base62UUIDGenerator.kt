package dev.wayron.book_tracker_api.utils

import com.github.f4b6a3.uuid.codec.base.Base62Codec
import java.util.*

object Base62UUIDGenerator {
  fun generate(): String {
    val id = Base62Codec.INSTANCE.encode(UUID.randomUUID())
    return id
  }

  fun generate(length: Int): String {
    val id = Base62Codec.INSTANCE.encode(UUID.randomUUID()).substring(0, length.coerceAtMost(22))
    return id
  }

  fun decode(input: String): UUID {
    val uuid = Base62Codec.INSTANCE.decode(input)
    return uuid
  }
}