package models.daos

import java.sql.Timestamp
import com.google.inject.AbstractModule
import com.mohiva.play.silhouette.api.LoginInfo
import com.typesafe.config.ConfigFactory
import models.persistence.User
import net.codingwell.scalaguice.ScalaModule
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import play.api.Configuration
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.{ PlaySpecification, WithApplication }
import scala.concurrent.Await
import scala.concurrent.duration._

/**
 * Test case for the [[models.daos.UserDAO]] class.
 */
class UserDAOSpec extends PlaySpecification with Mockito {
  sequential
  "UserDAO" should {

    "save and find a user by Id" in new Context {
      new WithApplication(application) {
        val userDao = application.injector.instanceOf(classOf[UserDAO])
        private val now: Timestamp = new Timestamp(System.currentTimeMillis())
        Await.result(
          userDao.save(User(0, LoginInfo("", ""), Some("name"), None, None, None, None, true, true, now, now)), 1 minute)
        val u: Option[User] = Await.result(
          userDao.find(1), 1 minute)

        u.get.id shouldEqual 1
        u.get.firstName shouldEqual Some("name")
      }
    }

    "save and find a user by LoginInfo" in new Context {
      new WithApplication(application) {
        val userDao = application.injector.instanceOf(classOf[UserDAO])
        private val now: Timestamp = new Timestamp(System.currentTimeMillis())
        private val loginInfo: LoginInfo = LoginInfo("key1", "v1")
        Await.result(
          userDao.save(User(0, loginInfo, Some("name"), None, None, None, None, true, true, now, now)), 1 minute)
        val u: Option[User] = Await.result(
          userDao.find(loginInfo), 1 minute)

        u.get.id shouldEqual 1
        u.get.firstName shouldEqual Some("name")
      }
    }

    "update existing user" in new Context {
      new WithApplication(application) {
        val userDao = application.injector.instanceOf(classOf[UserDAO])
        private val now: Timestamp = new Timestamp(System.currentTimeMillis())
        Await.result(
          userDao.save(User(0, LoginInfo("", ""), Some("name"), None, None, None, None, true, true, now, now)), 1 minute)
        Await.result(
          userDao.save(User(1, LoginInfo("key", "value"), Some("name1"), None, None, None, None, true, true, now, now)), 1 minute)
        val u: Option[User] = Await.result(
          userDao.find(1), 1 minute)

        u.get.id shouldEqual 1
        u.get.firstName shouldEqual Some("name1")
        u.get.loginInfo shouldEqual LoginInfo("key", "value")
      }
    }
  }

  /**
   * The context.
   */
  trait Context extends Scope {

    /**
     * A fake Guice module.
     */
    class FakeModule extends AbstractModule with ScalaModule {
      def configure() = {
        bind[UserDAO].to[UserDAOImpl]
      }
    }

    /**
     * The application.
     */
    lazy val application = new GuiceApplicationBuilder()
      .overrides(new FakeModule)
      .loadConfig(Configuration(ConfigFactory.load("application.test.conf")))
      .build()
  }

}
