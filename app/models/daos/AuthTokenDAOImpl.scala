package models.daos

import javax.inject.Inject
import java.sql.Timestamp

import models.persistence.{ AuthToken, AuthTokensTable, User, UsersTable }
import org.joda.time.DateTime
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ ExecutionContext, Future }

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
  override def find(id: Long)(implicit ec: ExecutionContext): Future[Option[AuthToken]] = findById(id)

  /**
   * Finds expired tokens.
   *
   * @param dateTime The current date time.
   */
  override def findExpired(dateTime: DateTime)(implicit ec: ExecutionContext): Future[Seq[AuthToken]] = findByFilter(token => token.expiry < new Timestamp(dateTime.getMillis))

  /**
   * Removes the token for the given ID.
   *
   * @param id The ID for which the token should be removed.
   * @return A future to wait for the process to be completed.
   */
  override def remove(id: Long)(implicit ec: ExecutionContext): Future[Unit] = deleteById(id).map(i => {})

  /**
   * Saves a token.
   *
   * @param token The token to save.
   * @return The saved token.
   */
  override def save(token: AuthToken)(implicit ec: ExecutionContext): Future[AuthToken] = insert(token).map(i => token)

}
