package services.persistence

import model._
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoPlugin
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.DefaultDB
import scala.concurrent.{ ExecutionContext, Future }

trait ShortUrlDao {
  def create(shortUrl: ShortUrl): Future[ShortUrl]
  def incrementAccessCount(shortUrl: ShortUrl): Future[ShortUrl]
  def findByToken(token: Token): Future[ShortUrl]
  def findByTarget(target: String): Future[ShortUrl]
}

trait ShortUrlRxMongoDao extends ShortUrlDao with ShortUrlPersistenceFormats {
  implicit val executionContext: ExecutionContext
  implicit val shortUrlFormats: Format[ShortUrl]
  def db: DefaultDB
  def shortUrls: JSONCollection = db("shortUrls")

  def create(shortUrl: ShortUrl): Future[ShortUrl] = {
    shortUrls.save(shortUrl) map {
      case r if !r.inError => shortUrl
      case error           => throw error
    }
  }

  def incrementAccessCount(shortUrl: ShortUrl): Future[ShortUrl] = {
    val command = Json.obj("$inc" -> Json.obj("accessCount" -> 1))
    shortUrls.update(ShortUrl.Selectors.byToken(shortUrl.token), command) map {
      case r if !r.inError => shortUrl.accessed
      case error           => throw error
    }
  }

  def findByToken(token: Token): Future[ShortUrl] = {
    shortUrls.find(ShortUrl.Selectors.byToken(token)).cursor.headOption map {
      case Some(shortUrl) => shortUrl
      case None           => throw new NoSuchElementException()
    }
  }

  def findByTarget(target: String): Future[ShortUrl] = {
    shortUrls.find(ShortUrl.Selectors.byTarget(target)).cursor.headOption map {
      case Some(shortUrl) => shortUrl
      case None           => throw new NoSuchElementException()
    }
  }
}

trait ShortUrlPersistenceFormats {
  implicit val shortUrlFormats = Json.format[ShortUrl]
}

object ShortUrlRxMongoDao extends ShortUrlRxMongoDao {

  import play.api.Play._

  def db = ReactiveMongoPlugin.db
  implicit val executionContext = play.api.libs.concurrent.Execution.defaultContext
}
