package dev.wayron.book_tracker_api.book.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import dev.wayron.book_tracker_api.book.converter.JsonConverter
import jakarta.persistence.*
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.sql.Timestamp
import java.time.LocalDate

@Entity
@Table(name = "books")
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
data class Book(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  var id: Int,

  @NotNull(message = "Title must not be blank")
  @NotBlank(message = "Title must not be blank")
  val title: String,

  @NotNull(message = "Author must not be blank")
  @NotBlank(message = "Author must not be blank")
  val author: String,

  @NotNull(message = "Pages must not be null")
  @Min(value = 1, message = "Pages must be greater than 0")
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
  var updatedAt: Timestamp  = Timestamp(System.currentTimeMillis()),
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