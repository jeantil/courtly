package plugins

import controllers.PlayAware
import akka.util.Timeout
import scala.concurrent.duration._
trait DomainAsker {
  this: PlayAware =>

  protected def domain: Domain
  private def configTimeout = app.configuration.getInt("domain.defaultTimeout").getOrElse(5)
  implicit val timeout: Timeout = configTimeout.seconds

}
