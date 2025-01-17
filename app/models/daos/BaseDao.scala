package models.daos

/**
 * Created by cdiniz on 29/09/16.
 */
import models.entities.BaseEntity
import play.api.db.slick.HasDatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.lifted._

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import java.sql.Timestamp

import models.persistence.tables.BaseTable

trait AbstractBaseDAO[T, A] {
  def insert(row: A): Future[Long]
  def insert(rows: Seq[A]): Future[Seq[Long]]
  def update(row: A): Future[Int]
  def update(rows: Seq[A]): Future[Unit]
  def findById(id: Long): Future[Option[A]]
  def findByFilter[C: CanBeQueryCondition](f: (T) => C): Future[Seq[A]]
  def deleteById(id: Long): Future[Int]
  def deleteById(ids: Seq[Long]): Future[Int]
  def deleteByFilter[C: CanBeQueryCondition](f: (T) => C): Future[Int]
}

abstract class BaseDAO[T <: BaseTable[A], A <: BaseEntity]() extends AbstractBaseDAO[T, A] with HasDatabaseConfigProvider[JdbcProfile] {
  import dbConfig.driver.api._

  val tableQ: TableQuery[T]

  def insert(row: A): Future[Long] = {
    insert(Seq(row)).map(_.head)
  }

  def insert(rows: Seq[A]): Future[Seq[Long]] = {
    db.run(tableQ returning tableQ.map(_.id) ++= rows.filter(_.isValid))
  }

  def update(row: A): Future[Int] = {
    if (row.isValid)
      db.run(tableQ.filter(_.id === row.id).update(row))
    else
      Future { 0 }
  }

  def update(rows: Seq[A]): Future[Unit] = {
    db.run(DBIO.seq((rows.filter(_.isValid).map(r => tableQ.filter(_.id === r.id).update(r))): _*))
  }

  def findById(id: Long): Future[Option[A]] = {
    db.run(tableQ.filter(_.id === id).result.headOption)
  }

  def findByFilter[C: CanBeQueryCondition](f: (T) => C): Future[Seq[A]] = {
    db.run(tableQ.withFilter(f).result)
  }

  def deleteById(id: Long): Future[Int] = {
    deleteById(Seq(id))
  }

  def deleteById(ids: Seq[Long]): Future[Int] = {
    db.run(tableQ.filter(_.id.inSet(ids)).delete)
  }

  def deleteByFilter[C: CanBeQueryCondition](f: (T) => C): Future[Int] = {
    db.run(tableQ.withFilter(f).delete)
  }

}
