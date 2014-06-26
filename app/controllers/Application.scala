package controllers

import scala.concurrent.Await

import play.api.Logger
import play.api.mvc._
import org.joda.time.DateTime
import services.persistence.ShortUrlRxMongoDao
import scala.concurrent.duration._

trait Application extends Controller {
  val logger = Logger("controllers.Application")

  def ping = Action { implicit request =>
    val startTime = System.currentTimeMillis()
    val mongoping = Await.result(ShortUrlRxMongoDao.findFirst().map(_= System.currentTimeMillis() - startTime ), 10.seconds)
    Ok(play.api.libs.json.Json.obj(
      "name" -> io.courtly.BuildInfo.name,
      "version" -> io.courtly.BuildInfo.version,
      "timestamp" -> new DateTime(),
      "mongoping" -> mongoping
    )).withHeaders("Cache-Control" -> "no-cache")
  }
}
object Application extends Application
