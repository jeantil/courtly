package controllers

import java.net.URL

import play.api.mvc._
import play.api.libs.json.Json
import play.api.Logger
import scala.concurrent.Future

import services.persistence.ShortUrlDao
import model.Token
import services.TokenGenerator
import reactivemongo.core.errors.DatabaseException

abstract class ShortUrl extends Controller with PlayLogging {

  def shortUrlDao: ShortUrlDao
  def tokenGenerator: TokenGenerator

  def shorten = Action.async(parse.json) { implicit request =>
    (request.body \ "target").validate[String].fold(
      errors => Future.successful(jsonErrors2BadRequest(errors, log)),
      target => {
        shortUrlDao.findByTarget(target)
          .recoverWith {
            case t: NoSuchElementException =>
              val result = createShortUrl(target)
              result.onSuccess { case createdShortUrl => log.info(s"${request.remoteAddress} accessed $request -> $createdShortUrl ") }
              result
          }
          .map(shortUrl => Ok(controllers.routes.ShortUrl.resolve(shortUrl.token.value).absoluteURL(sslEnabled)))
          .recover {
            case throwable =>
              log.warn(s"Unable to resolve a target url for $target.", throwable)
              BadRequest
          }
      }
    )
  }

  def createShortUrl(target: String, recurseCount: Int = 0, tokenLength: Option[Int] = None): Future[model.ShortUrl] = {
    val token = tokenLength map (l => tokenGenerator.generateToken(length = l)) getOrElse tokenGenerator.generateToken()
    val shortUrl: model.ShortUrl = model.ShortUrl(target, token)
    val fShortUrl = shortUrlDao.create(shortUrl)
    fShortUrl.recoverWith { case t: DatabaseException if recurseCount < 10 => createShortUrl(target, recurseCount + 1, tokenLength = Some(token.length)) }
  }

  def resolve(token: String) = Action.async { request =>
    val result = for {
      shortUrl <- shortUrlDao.findByToken(Token(token))
      accessedShortUrl <- shortUrlDao.incrementAccessCount(shortUrl)
    } yield {
      log.info(s"${request.remoteAddress} accessed $request -> $accessedShortUrl ")
      TemporaryRedirect(new java.net.URI(accessedShortUrl.target).toASCIIString)
    }
    result.recover {
      case throwable =>
        log.warn(s"Unable to resolve a target url for $token.", throwable)
        BadRequest
    }
  }
  def stats(token: String) = Action.async { request =>
    val result = for {
      shortUrl <- shortUrlDao.findByToken(Token(token))
    } yield {
      Ok(Json.toJson(shortUrl))
    }
    result.recover {
      case throwable =>
        log.warn(s"Unable to resolve a target url for $token.", throwable)
        BadRequest
    }
  }
}
object ShortUrl extends ShortUrl() {
  import play.api
  import services.persistence.ShortUrlRxMongoDao
  import services.token.RandomTokenGenerator

  override def log = Logger("application.controllers.ShortUrl")
  override protected implicit def app: api.Application = api.Play.current
  override def shortUrlDao: ShortUrlDao = ShortUrlRxMongoDao
  override def tokenGenerator = RandomTokenGenerator()

}
