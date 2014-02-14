package controllers

import akka.pattern.ask
import model.command.ShortenUrl
import model.event._
import model.query._
import play.api.mvc._
import play.api.Logger
import plugins.{ DomainAsker, Domain }
import scala.concurrent.Future

abstract class ShortUrl extends Controller with PlayLogging with DomainAsker {

  def shorten = Action.async(parse.json) { implicit request =>
    (request.body \ "target").validate[String].fold(
      errors => Future.successful(jsonErrors2BadRequest(errors, log)),
      target => (domain.shortUrlRegistry ? ShortenUrl(target)) map {
        case ShortUrlCreated(shortUrl) => Ok(controllers.routes.ShortUrl.resolve(shortUrl.token).absoluteURL(sslEnabled))
        case m =>
          log.debug(s"Unable to create a short url for $target. Domain returned $m")
          BadRequest
      }
    )
  }
  def resolve(token: String) = Action.async { request =>
    (domain.shortUrlRegistry ? ResolveToken(token)) map {
      case ShortUrlNotFound        => NotFound
      case ShortUrlFound(shortUrl) => MovedPermanently(shortUrl.target)
      case m =>
        log.debug(s"Unable to resolve a target url for $token. Domain returned $m")
        BadRequest
    }
  }
}
object ShortUrl extends ShortUrl() {
  import play.api.Play.current
  import play.api.PlayException
  import play.api

  override def domain = current.plugin[Domain].getOrElse(throw new PlayException("Domain plugin is not initialized", "Make sure the Domain plugin is defined in conf/play.plugin and is enabled in application.conf"))
  override def log = Logger("application.controllers.ShortUrl")
  override protected implicit def app: api.Application = current
}