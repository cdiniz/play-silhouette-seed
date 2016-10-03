package models.persistence

import java.sql.Timestamp
import java.util.UUID

import models.entities.BaseEntity
import org.joda.time.DateTime

/**
 * A token to authenticate a user against an endpoint for a short time period.
 *
 * @param token The unique token ID.
 * @param userId The unique ID of the user the token is associated with.
 * @param expiry The date-time the token expires.
 */
case class AuthToken(
  id: Long,
  token: UUID,
  userId: Long,
  expiry: DateTime,
  createdAt: Timestamp,
  editedAt: Timestamp) extends BaseEntity
