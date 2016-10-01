package models.daos

import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import models.persistence.{ User, UsersTable }
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ ExecutionContext, Future }

/**
 * Created by cdiniz on 01/10/16.
 */
class UserDAOImpl @Inject() (override protected val dbConfigProvider: DatabaseConfigProvider) extends BaseDAO[UsersTable, User] with UserDAO {
  import dbConfig.driver.api._

  override val tableQ: TableQuery[UsersTable] = TableQuery[UsersTable]

  /**
   * Finds a user by its login info.
   *
   * @param loginInfo The login info of the user to find.
   * @return The found user or None if no user for the given login info could be found.
   */
  override def find(loginInfo: LoginInfo)(implicit ec: ExecutionContext): Future[Option[User]] = findByFilter(user => user.providerKey === loginInfo.providerKey && user.providerId === loginInfo.providerID).map(_.headOption)

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  override def save(user: User)(implicit ec: ExecutionContext): Future[User] = insert(user).map(i => user)

  /**
   * Finds a user by its user ID.
   *
   * @param userId The ID of the user to find.
   * @return The found user or None if no user for the given ID could be found.
   */
  override def find(userId: Long)(implicit ec: ExecutionContext): Future[Option[User]] = findById(userId)
}
