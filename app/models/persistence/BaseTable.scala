package models.persistence

import java.sql.Timestamp

import slick.driver.H2Driver.api._

/**
 * Created by cdiniz on 29/09/16.
 */

abstract class BaseTable[T](tag: Tag, name: String) extends Table[T](tag, name) {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def createdAt = column[Timestamp]("created_at")
}
