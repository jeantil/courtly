package model

import play.api.libs.json._

case class ShortUrl(target: String, token: Token, accessCount: Int = 0) {
  def accessed = copy(accessCount = accessCount + 1)
}

object ShortUrl extends ShortUrlRxMongo {
  implicit val ShortUrlFormat = Json.format[ShortUrl]
}

trait ShortUrlRxMongo {

  import reactivemongo.api.indexes.{ IndexType, Index }

  val ShortUrlByTokenIndex = Index(
    List("token" -> IndexType.Ascending),
    Some("shortUrlByToken"),
    unique = true,
    background = true,
    dropDups = false,
    sparse = false
  )
  val ShortUrlByTargetIndex = Index(
    List("target" -> IndexType.Ascending),
    Some("shortUrlByTarget"),
    unique = true,
    background = true,
    dropDups = false,
    sparse = false
  )
  object Selectors {
    def byToken(token: Token) = {
      Json.obj("token" -> token)
    }
    def byTarget(target: String) = {
      Json.obj("target" -> target)
    }
  }
}
