package plugins

import play.api._
import akka.actor.ActorSystem
import actors.ShortUrlRegistry

class Domain(app: play.api.Application) extends Plugin {
  import Domain._

  val log = Logger("application.plugins.Domain")

  var appSystem: Option[ActorSystem] = None

  var isRunning = false
  override def onStart(): Unit = {
    import com.typesafe.config.{ Config, ConfigFactory }
    val domainConfig: Config = ConfigFactory.load().getConfig("domain")
    val initializingSystem: ActorSystem = ActorSystem("domain", domainConfig)
    initializingSystem.actorOf(ShortUrlRegistry.props(), ShortUrlRegistry.name)
    appSystem = Some(initializingSystem)

  }

  override def onStop(): Unit = {
    val dyingSystem = appSystem
    appSystem = None
    dyingSystem map (_.shutdown())
    dyingSystem map (_.awaitTermination())
  }

  override def enabled: Boolean = app.configuration.getBoolean(ConfigKeys.DOMAIN_ENABLED).getOrElse(true)

  def system = {
    appSystem.get
  }

  def shortUrlRegistry = system.actorSelection(ShortUrlRegistry.path)
}
object Domain {
  object ConfigKeys {
    val DOMAIN_ENABLED = "domain.enabled"
  }
}