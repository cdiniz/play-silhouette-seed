package models.persistence

import java.sql.Timestamp

import com.mohiva.play.silhouette.api.{ Identity, LoginInfo }
import models.entities.BaseEntity

/**
 * The user object.
 *
 * @param id The database ID of the user.
 * @param loginInfo The linked login info.
 * @param firstName Maybe the first name of the authenticated user.
 * @param lastName Maybe the last name of the authenticated user.
 * @param fullName Maybe the full name of the authenticated user.
 * @param email Maybe the email of the authenticated provider.
 * @param avatarURL Maybe the avatar URL of the authenticated provider.
 * @param activated Indicates that the user has activated its registration.
 * @param explicitContentAuth Indicates that the user can see explicit content.
 * @param createdAt Indicates the creation timestamp.
 * @param editedAt Indicates the edit timestamp.
 */
case class User(
  id: Long,
  loginInfo: LoginInfo,
  firstName: Option[String],
  lastName: Option[String],
  fullName: Option[String],
  email: Option[String],
  avatarURL: Option[String],
  activated: Boolean,
  explicitContentAuth: Boolean,
  createdAt: Timestamp,
  editedAt: Timestamp) extends Identity with BaseEntity {

  /**
   * Tries to construct a name.
   *
   * @return Maybe a name.
   */
  def name = fullName.orElse {
    firstName -> lastName match {
      case (Some(f), Some(l)) => Some(f + " " + l)
      case (Some(f), None) => Some(f)
      case (None, Some(l)) => Some(l)
      case _ => None
    }
  }
}
