package dev.wayron.book_tracker_api.modules.models.book

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import dev.wayron.book_tracker_api.modules.models.user.User
import dev.wayron.book_tracker_api.utils.JsonConverter
import jakarta.persistence.*
import java.sql.Timestamp
import java.time.LocalDate

@Entity
@Table(name = "books")
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
data class Book(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  var id: Int,

  val title: String,
  val author: String,
  val pages: Int,
  val chapters: Int? = null,

  val coverUrl: String? = null,

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  var userId: User,

  val synopsis: String? = null,
  val publisher: String? = null,
  val publicationDate: LocalDate? = null,
  val language: String? = null,
  val isbn10: String? = null,
  val isbn13: String? = null,
  val typeOfMedia: String? = null,

  @Convert(converter = JsonConverter::class)
  val genres: List<String>? = emptyList(),

  @Column(name = "created_at", updatable = false)
  var createdAt: Timestamp = Timestamp(System.currentTimeMillis()),
  var updatedAt: Timestamp = Timestamp(System.currentTimeMillis()),
) {

  @PrePersist
  protected fun onCreate() {
    val now = Timestamp(System.currentTimeMillis())
    this.createdAt = now
    this.updatedAt = now
  }

  @PreUpdate
  protected fun onUpdate() {
    this.updatedAt = Timestamp(System.currentTimeMillis())
  }

}