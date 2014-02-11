package controllers

import play.api.mvc._
import org.joda.time.DateTime

trait Application extends Controller {
  def ping = Action { implicit request =>
    Ok(play.api.libs.json.Json.obj(
      "name" -> io.courtly.BuildInfo.name,
      "version" -> io.courtly.BuildInfo.version,
      "timestamp" -> new DateTime()
    )).withHeaders("Cache-Control" -> "no-cache")
  }
}
object Application extends Application
