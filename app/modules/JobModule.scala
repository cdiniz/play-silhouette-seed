package modules

import jobs.{ AuthTokenCleaner, Scheduler }
import models.daos.{ AuthTokenDAO, AuthTokenDAOImpl }
import net.codingwell.scalaguice.ScalaModule
import play.api.libs.concurrent.AkkaGuiceSupport

/**
 * The job module.
 */
class JobModule extends ScalaModule with AkkaGuiceSupport {
  @Override
  def configure() = {
    bindActor[AuthTokenCleaner]("auth-token-cleaner")
    bind[Scheduler].asEagerSingleton()
  }
}
