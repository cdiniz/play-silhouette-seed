package models.daos

import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import models.persistence.User
import models.persistence.tables.UsersTable
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ ExecutionContext, Future }
import play.api.libs.concurrent.Execution.Implicits.defaultContext

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
  override def find(loginInfo: LoginInfo): Future[Option[User]] = findByFilter(user => user.providerKey === loginInfo.providerKey && user.providerId === loginInfo.providerID).map(_.headOption)

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  override def save(user: User): Future[User] = {
    if (user.id == 0) insert(user).map { id: Long => user.copy(id = id) } else update(user).map(_ => user)
  }

  /**
   * Finds a user by its user ID.
   *
   * @param userId The ID of the user to find.
   * @return The found user or None if no user for the given ID could be found.
   */
  override def find(userId: Long): Future[Option[User]] = findById(userId)
}
