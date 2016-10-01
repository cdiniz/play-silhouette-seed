package models.persistence

import java.sql.Timestamp
import java.util.UUID

import slick.driver.H2Driver.api._
import org.joda.time.DateTime

/**
 * Created by cdiniz on 30/09/16.
 */
class AuthTokensTable(tag: Tag) extends BaseTable[AuthToken](tag, "auth_tokens") {
  def token = column[UUID]("token")
  def userId = column[Long]("user_id")
  def expiry = column[Timestamp]("expiry")

  def user = foreignKey(
    "oauth_tokens_users_fk",
    userId,
    TableQuery[UsersTable])(_.id)

  def * = (id, token, userId, expiry, createdAt, editedAt).shaped <>
    ({
      case (id: Long, token: UUID, userId: Long, expiry: Timestamp, createdAt: Timestamp, editedAt: Timestamp) => AuthToken(id, token, userId, new DateTime(expiry.getTime), createdAt, editedAt)
    },
      {
        authToken: AuthToken => Some((authToken.id, authToken.token, authToken.userId, new Timestamp(authToken.expiry.getMillis), authToken.createdAt, authToken.editedAt))
      })
}

