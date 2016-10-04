package models.services

import java.sql.Timestamp

import scala.concurrent.duration._
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.Clock
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import models.persistence.User
import models.daos.UserDAO
import org.joda.time.DateTime

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.test.PlaySpecification
import org.specs2.mock.Mockito

import scala.concurrent.{Await, ExecutionContext, Future}
/**
  * Created by cdiniz on 03/10/16.
  */
class UserServiceTest(implicit ec: ExecutionContext) extends PlaySpecification with Mockito{


  val sampleUser = User(1,LoginInfo("",""),None,None,None,None,None,true,true,new Timestamp(Clock().now.getMillis),new Timestamp(Clock().now.getMillis))

  "UserService" should {

    "retrieve user by id" in  {
      val daoMock = mock[UserDAO]
      val clock = mock[Clock]
      val service = new UserServiceImpl(daoMock,clock)
      val returnUser = Future.successful{ Some(sampleUser)}
      daoMock.find(1) returns returnUser

      service.retrieve(1) shouldEqual returnUser

      there was one(daoMock).find(1)
    }

    "retrieve user by loginInfo" in  {
      val daoMock = mock[UserDAO]
      val clock = mock[Clock]
      val service = new UserServiceImpl(daoMock,clock)
      val returnUser = Future.successful{ Some(sampleUser)}
      val loginInfo = LoginInfo("id","key")
      daoMock.find(loginInfo) returns returnUser

      service.retrieve(loginInfo) shouldEqual returnUser

      there was one(daoMock).find(loginInfo)
    }

    "save user" in  {
      val daoMock = mock[UserDAO]
      val clock = mock[Clock]
      val service = new UserServiceImpl(daoMock,clock)
      val returnUser = Future.successful{sampleUser}
      val loginInfo = LoginInfo("id","key")
      daoMock.save(sampleUser) returns returnUser

      service.save(sampleUser) shouldEqual returnUser

      there was one(daoMock).save(sampleUser)
    }

    "save an existing user with commonSocialProfile" in {
      val daoMock = mock[UserDAO]
      val clock = mock[Clock]
      val service = new UserServiceImpl(daoMock,clock)
      val returnUser = Future.successful{Some(sampleUser)}
      val loginInfo = LoginInfo("id","key")
      val commonsProfile = CommonSocialProfile(loginInfo,Some("first"),Some("last"),Some("full"),Some("email"),Some("avatar"))
      val userWithProfileInfo = sampleUser.copy( firstName = Some("first"), lastName = Some("last"), fullName = Some("full"), email = Some("email"), avatarURL = Some("avatar"))
      val returnUserWithProfileInfo = Future{userWithProfileInfo}
      daoMock.find(loginInfo) returns returnUser
      daoMock.save(userWithProfileInfo) returns returnUserWithProfileInfo

      Await.result(service.save(commonsProfile),1 minute) shouldEqual userWithProfileInfo

      there was one(daoMock).find(loginInfo)
      there was one(daoMock).save(userWithProfileInfo)

    }

    "save a new user with commonSocialProfile" in {
      val daoMock = mock[UserDAO]
      val clock = mock[Clock]
      val service = new UserServiceImpl(daoMock,clock)
      val returnNone = Future.successful{None}
      val loginInfo = LoginInfo("id","key")
      val userCreationTime = DateTime.now
      val userCreationInstant = new Timestamp(DateTime.now.getMillis)
      val commonsProfile = CommonSocialProfile(loginInfo,Some("first"),Some("last"),Some("full"),Some("email"),Some("avatar"))
      val userWithProfileInfo = User(id = 0, loginInfo = loginInfo, firstName = Some("first"), lastName = Some("last"), fullName = Some("full"), email = Some("email"), avatarURL = Some("avatar"), activated = true, explicitContentAuth = false, userCreationInstant, userCreationInstant)
      val returnUserWithProfileInfo = Future{userWithProfileInfo}

      clock.now returns userCreationTime
      daoMock.find(loginInfo) returns returnNone
      daoMock.save(userWithProfileInfo) returns returnUserWithProfileInfo

      Await.result(service.save(commonsProfile),1 minute) shouldEqual userWithProfileInfo

      there was one(clock).now
      there was one(daoMock).find(loginInfo)
      there was one(daoMock).save(userWithProfileInfo)

    }

  }
}
