package models.entities

/**
 * Created by cdiniz on 29/09/16.
 */
trait BaseEntity {
  val id: Long
  def isValid = true
}
