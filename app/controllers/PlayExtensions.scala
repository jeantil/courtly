package controllers
import play.api.Logger
trait PlayAware {
  protected implicit def app: play.api.Application
  def sslEnabled = app.configuration.getBoolean("sslEnabled").getOrElse(false)
}
trait PlayLogging extends PlayAware {
  protected def log: Logger
}
trait Controller extends play.api.mvc.Controller {
  implicit val executionContext = play.api.libs.concurrent.Execution.defaultContext
}

