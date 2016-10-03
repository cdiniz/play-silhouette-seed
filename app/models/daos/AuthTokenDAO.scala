package models.daos

import java.util.UUID

import models.persistence.AuthToken
import org.joda.time.DateTime

import scala.concurrent.{ ExecutionContext, Future }

/**
 * Give access to the [[models.persistence.AuthToken]] object.
 */
trait AuthTokenDAO {

  /**
   * Finds a token by its ID.
   *
   * @param id The unique token ID.
   * @return The found token or None if no token for the given ID could be found.
   */
  def find(id: UUID)(implicit ec: ExecutionContext): Future[Option[AuthToken]]

  /**
   * Finds expired tokens.
   *
   * @param dateTime The current date time.
   */
  def findExpired(dateTime: DateTime)(implicit ec: ExecutionContext): Future[Seq[AuthToken]]

  /**
   * Saves a token.
   *
   * @param token The token to save.
   * @return The saved token.
   */
  def save(token: AuthToken)(implicit ec: ExecutionContext): Future[AuthToken]

  /**
   * Removes the token for the given ID.
   *
   * @param id The ID for which the token should be removed.
   * @return A future to wait for the process to be completed.
   */
  def remove(id: UUID)(implicit ec: ExecutionContext): Future[Unit]
}
