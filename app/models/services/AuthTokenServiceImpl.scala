package models.services

import java.sql.Timestamp
import javax.inject.Inject

import com.mohiva.play.silhouette.api.util.Clock
import models.daos.AuthTokenDAO
import models.persistence.AuthToken
import org.joda.time.DateTimeZone
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

/**
 * Handles actions to auth tokens.
 *
 * @param authTokenDAO The auth token DAO implementation.
 * @param clock The clock instance.
 */
class AuthTokenServiceImpl @Inject() (authTokenDAO: AuthTokenDAO, clock: Clock) extends AuthTokenService {

  /**
   * Creates a new auth token and saves it in the backing store.
   *
   * @param userId The user ID for which the token should be created.
   * @param expiry The duration a token expires.
   * @return The saved auth token.
   */
  def create(userId: Long, expiry: FiniteDuration = 5 minutes) = {
    val now = new Timestamp(clock.now.getMillis);
    val token = AuthToken(clock.now.getMillis, userId, clock.now.withZone(DateTimeZone.UTC).plusSeconds(expiry.toSeconds.toInt), now, now)
    authTokenDAO.save(token)
  }

  /**
   * Validates a token ID.
   *
   * @param id The token ID to validate.
   * @return The token if it's valid, None otherwise.
   */
  def validate(id: Long) = authTokenDAO.find(id)

  /**
   * Cleans expired tokens.
   *
   * @return The list of deleted tokens.
   */
  def clean = authTokenDAO.findExpired(clock.now.withZone(DateTimeZone.UTC)).flatMap { tokens =>
    Future.sequence(tokens.map { token =>
      authTokenDAO.remove(token.id).map(_ => token)
    })
  }
}
