package models.persistence
import slick.driver.H2Driver.api._
import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo

/**
 * Created by cdiniz on 30/09/16.
 */
class UsersTable(tag: Tag) extends BaseTable[User](tag, "users") {
  def userID = column[UUID]("user_id")
  def providerId = column[String]("provider_id")
  def providerKey = column[String]("provider_key")
  def firstName = column[String]("first_name")
  def lastName = column[String]("last_name")
  def fullName = column[String]("full_name")
  def email = column[String]("email")
  def avatarURL = column[String]("avatar_url")
  def activated = column[Boolean]("activated")
  def explicitContentAuth = column[Boolean]("explicit_content_auth")

  def uniqueEmail = index("IDX_EMAIL", email, unique = true)

  def * = (id, (providerId, providerKey), firstName, lastName, fullName, email, avatarURL, activated, explicitContentAuth, createdAt, editedAt).shaped <>
    ({
      case (id, loginInfo, firstName, lastName, fullName, email, avatarUrl, activated, explicitContentAuth, createdAt, editedAt) =>
        User(id, LoginInfo.tupled.apply(loginInfo), Option(firstName), Option(lastName), Option(fullName),
          Option(email), Option(avatarUrl), activated, explicitContentAuth, createdAt, editedAt)
    },
      {
        u: User =>
          Some((
            u.id,
            (LoginInfo.unapply(u.loginInfo).get),
            u.firstName.getOrElse(""),
            u.lastName.getOrElse(""),
            u.fullName.getOrElse(""),
            u.email.getOrElse(""),
            u.avatarURL.getOrElse(""),
            u.activated,
            u.explicitContentAuth,
            u.createdAt,
            u.editedAt))
      }
    )
}
