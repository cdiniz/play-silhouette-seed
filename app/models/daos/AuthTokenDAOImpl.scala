package models.daos

import javax.inject.Inject
import java.sql.Timestamp
import java.util.UUID

import models.persistence.tables.AuthTokensTable
import models.persistence.AuthToken
import org.joda.time.DateTime
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

/**
 * Created by cdiniz on 01/10/16.
 */
class AuthTokenDAOImpl @Inject() (override protected val dbConfigProvider: DatabaseConfigProvider) extends BaseDAO[AuthTokensTable, AuthToken] with AuthTokenDAO {
  import dbConfig.driver.api._

  override val tableQ: TableQuery[AuthTokensTable] = TableQuery[AuthTokensTable]

  /**
   * Finds a token by its ID.
   *
   * @param id The unique token ID.
   * @return The found token or None if no token for the given ID could be found.
   */
  override def find(id: UUID): Future[Option[AuthToken]] = findByFilter(_.token === id).map(_.headOption)

  /**
   * Finds expired tokens.
   *
   * @param dateTime The current date time.
   */
  override def findExpired(dateTime: DateTime): Future[Seq[AuthToken]] = findByFilter(token => token.expiry < new Timestamp(dateTime.getMillis))

  /**
   * Removes the token for the given ID.
   *
   * @param id The ID for which the token should be removed.
   * @return A future to wait for the process to be completed.
   */
  override def remove(id: UUID): Future[Unit] = deleteByFilter(_.token === id).map(i => {})

  /**
   * Saves a token.
   *
   * @param token The token to save.
   * @return The saved token.
   */
  override def save(token: AuthToken): Future[AuthToken] = insert(token).map(id => token.copy(id = id))

}
