package models.services

import java.sql.Timestamp
import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.Clock
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import models.daos.{ AuthTokenDAO, UserDAO }
import models.persistence.{ AuthToken, User }
import org.joda.time.DateTime
import org.specs2.mock.Mockito
import play.api.test.PlaySpecification

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{ Await, ExecutionContext, Future }

/**
 * Created by cdiniz on 05/10/16.
 */
class AuthTokenServiceSpec(implicit ec: ExecutionContext) extends PlaySpecification with Mockito {
  val sampleDateTime = DateTime.now
  val sampleTokenId = UUID.randomUUID()
  val sampleToken = AuthToken(0, sampleTokenId, 1, sampleDateTime.plusSeconds((5 minutes).toSeconds.toInt), new Timestamp(sampleDateTime.getMillis), new Timestamp(sampleDateTime.getMillis))

  "AuthTokenService" should {

    "create a token for a user" in {
      val daoMock = mock[AuthTokenDAO]
      val clock = mock[Clock]
      val service = new AuthTokenServiceImpl(daoMock, clock)
      val returnToken = Future.successful { sampleToken }
      clock.now returns sampleDateTime
      daoMock.save(sampleToken) returns returnToken

      service.create(1, sampleTokenId) shouldEqual returnToken

      there was one(clock).now
      there was one(daoMock).save(sampleToken)
    }

    "validates a token" in {
      val daoMock = mock[AuthTokenDAO]
      val clock = mock[Clock]
      val service = new AuthTokenServiceImpl(daoMock, clock)
      val returnToken = Future.successful { Some(sampleToken) }
      daoMock.find(sampleTokenId) returns returnToken

      service.validate(sampleTokenId) shouldEqual returnToken

      there was one(daoMock).find(sampleTokenId)
    }

    "clean tokens without tokens to cleanup" in {
      val daoMock = mock[AuthTokenDAO]
      val clock = mock[Clock]
      val service = new AuthTokenServiceImpl(daoMock, clock)
      val returnToken = Future.successful { Some(sampleToken) }
      val returnTokens = Future.successful { Seq() }

      clock.now returns sampleDateTime
      daoMock.findExpired(sampleDateTime) returns returnTokens
      Await.result(service.clean, 1 minute) shouldEqual Seq()

      there was one(daoMock).findExpired(sampleDateTime)
      there was one(clock).now
    }

    "clean tokens" in {
      val daoMock = mock[AuthTokenDAO]
      val clock = mock[Clock]
      val service = new AuthTokenServiceImpl(daoMock, clock)
      val returnToken = Future.successful { Some(sampleToken) }
      val returnTokens = Future.successful { Seq(sampleToken) }

      clock.now returns sampleDateTime
      daoMock.findExpired(sampleDateTime) returns returnTokens
      daoMock.remove(sampleToken.token) returns Future.successful({})

      Await.result(service.clean, 1 minute) shouldEqual Seq(sampleToken)

      there was one(daoMock).findExpired(sampleDateTime)
      there was one(daoMock).remove(sampleToken.token)
      there was one(clock).now
    }

  }
}
