package models.persistence

import java.sql.Timestamp

import slick.driver.H2Driver.api._

import org.joda.time.DateTime

/**
 * Created by cdiniz on 30/09/16.
 */
class AuthTokensTable(tag: Tag) extends BaseTable[AuthToken](tag, "auth_tokens") {
  def userId = column[Long]("user_id")
  def expiry = column[Timestamp]("expiry")

  def * = (id, userId, expiry, createdAt, editedAt).shaped <>
    ({
      case (id: Long, userId: Long, expiry: Timestamp, createdAt: Timestamp, editedAt: Timestamp) => AuthToken(id, userId, new DateTime(expiry.getTime), createdAt, editedAt)
    },
      {
        authToken: AuthToken => Some((authToken.id, authToken.userId, new Timestamp(authToken.expiry.getMillis), authToken.createdAt, authToken.editedAt))
      })
}

