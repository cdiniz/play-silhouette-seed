package models.daos

import java.sql.Timestamp
import java.util.UUID

import com.google.inject.AbstractModule
import com.mohiva.play.silhouette.api.LoginInfo
import com.typesafe.config.ConfigFactory
import models.persistence.{ AuthToken, User }
import net.codingwell.scalaguice.ScalaModule
import org.joda.time.DateTime
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import play.api.Configuration
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.{ PlaySpecification, WithApplication }

import scala.concurrent.Await
import scala.concurrent.duration._

/**
 * Test case for the [[models.daos.AuthTokenDAO]] class.
 */
class AuthTokenDAOSpec extends PlaySpecification with Mockito {
  sequential
  "authTokenDAO" should {

    "try to save a token to inexisting user" in new Context {
      new WithApplication(application) {
        val tokenDao = application.injector.instanceOf(classOf[AuthTokenDAO])
        val now: Timestamp = new Timestamp(System.currentTimeMillis())
        val expiry: Timestamp = new Timestamp(System.currentTimeMillis() + 100000L)
        val id = UUID.randomUUID();
        Await.result(tokenDao.save(AuthToken(0, id, 1, DateTime.now.plusSeconds(1000), now, now)), 1 minute) must throwA[org.h2.jdbc.JdbcSQLException]
      }
    }

    "save and find a token by Id" in new Context {
      new WithApplication(application) {
        val userDao = application.injector.instanceOf(classOf[UserDAO])
        private val now: Timestamp = new Timestamp(System.currentTimeMillis())
        Await.result(
          userDao.save(User(0, LoginInfo("", ""), Some("name"), None, None, None, None, true, true, now, now)), 1 minute)
        val tokenDao = application.injector.instanceOf(classOf[AuthTokenDAO])
        val expiry: Timestamp = new Timestamp(System.currentTimeMillis() + 100000L)
        val id = UUID.randomUUID();
        Await.result(tokenDao.save(AuthToken(0, id, 1, DateTime.now.plusSeconds(1000), now, now)), 1 minute)
        val t: Option[AuthToken] = Await.result(
          tokenDao.find(id), 1 minute)

        t.get.id shouldEqual 1
        t.get.token shouldEqual id
      }
    }

    "find expired tokens" in new Context {
      new WithApplication(application) {
        val userDao = application.injector.instanceOf(classOf[UserDAO])
        private val now: Timestamp = new Timestamp(System.currentTimeMillis())
        Await.result(
          userDao.save(User(0, LoginInfo("", ""), Some("name"), None, None, None, None, true, true, now, now)), 1 minute)
        val tokenDao = application.injector.instanceOf(classOf[AuthTokenDAO])
        val expiry: Timestamp = new Timestamp(System.currentTimeMillis() + 100000L)
        val id = UUID.randomUUID();
        val idOfExpiredToken = UUID.randomUUID();
        Await.result(tokenDao.save(AuthToken(0, id, 1, DateTime.now.plusSeconds(10000), now, now)), 1 minute)
        Await.result(tokenDao.save(AuthToken(0, idOfExpiredToken, 1, DateTime.now.plusSeconds(0), now, now)), 1 minute)
        val tokens: Seq[AuthToken] = Await.result(
          tokenDao.findExpired(DateTime.now.plusSeconds(10)), 1 minute)

        tokens.length shouldEqual 1
        tokens.head.token shouldEqual idOfExpiredToken
      }
    }

    "remove existing token" in new Context {
      new WithApplication(application) {
        val userDao = application.injector.instanceOf(classOf[UserDAO])
        private val now: Timestamp = new Timestamp(System.currentTimeMillis())
        Await.result(
          userDao.save(User(0, LoginInfo("", ""), Some("name"), None, None, None, None, true, true, now, now)), 1 minute)
        val tokenDao = application.injector.instanceOf(classOf[AuthTokenDAO])
        val expiry: Timestamp = new Timestamp(System.currentTimeMillis() + 100000L)
        val id = UUID.randomUUID();
        Await.result(tokenDao.save(AuthToken(0, id, 1, DateTime.now.plusSeconds(1000), now, now)), 1 minute)
        Await.result(
          tokenDao.remove(id), 1 minute)
        val t: Option[AuthToken] = Await.result(
          tokenDao.find(id), 1 minute)

        t shouldEqual None
      }
    }

    "try to remove unexisting token" in new Context {
      new WithApplication(application) {
        val userDao = application.injector.instanceOf(classOf[UserDAO])
        private val now: Timestamp = new Timestamp(System.currentTimeMillis())
        Await.result(
          userDao.save(User(0, LoginInfo("", ""), Some("name"), None, None, None, None, true, true, now, now)), 1 minute)
        val tokenDao = application.injector.instanceOf(classOf[AuthTokenDAO])
        val expiry: Timestamp = new Timestamp(System.currentTimeMillis() + 100000L)
        val id = UUID.randomUUID();
        Await.result(tokenDao.save(AuthToken(0, id, 1, DateTime.now.plusSeconds(1000), now, now)), 1 minute)
        Await.result(
          tokenDao.remove(UUID.randomUUID()), 1 minute)
        val t: Option[AuthToken] = Await.result(
          tokenDao.find(id), 1 minute)

        t shouldNotEqual None
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
        bind[AuthTokenDAO].to[AuthTokenDAOImpl]
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
