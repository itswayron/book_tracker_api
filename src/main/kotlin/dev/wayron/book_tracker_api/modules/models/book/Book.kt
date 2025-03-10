package dev.wayron.book_tracker_api.modules.models.book

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
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
  val chapters: Int?,

  val synopsis: String?,
  val publisher: String?,
  val publicationDate: LocalDate?,
  val language: String?,
  val isbn10: String?,
  val isbn13: String?,
  val typeOfMedia: String?,

  @Convert(converter = JsonConverter::class)
  val genres: List<String>?,

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