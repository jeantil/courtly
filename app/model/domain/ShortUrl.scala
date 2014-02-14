package model.domain
import java.util.UUID

trait Base62TokenBuilder {
  def apply(uuidGen: () => UUID = UUID.randomUUID): String
}

case class ShortUrl(target: String, token: String, accessCount: Int = 0) {
  def incrementAccessCount = copy(accessCount = accessCount + 1)
}

object ShortUrl {

  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  implicit val ShortUrlFormat: Format[ShortUrl] = new Format[ShortUrl] {
    val format = (
      (__ \ "target").format[String] and
      (__ \ "token").format[String] and
      (__ \ "accessCount").format[Int]
    )((target, token, accessCount) => ShortUrl.apply(target, token, accessCount), unlift(ShortUrl.unapply))

    override def writes(o: ShortUrl): JsValue = format.writes(o)
    override def reads(json: JsValue): JsResult[ShortUrl] = format.reads(json)
  }
  def apply(target: String) = {
    new ShortUrl(target, Base62Token())
  }

  private[ShortUrl] case class Base62Token(token: String) extends AnyVal
  private[ShortUrl] object Base62Token extends Base62TokenBuilder {
    val ALPHABET: String = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_"

    private def divmod(number: BigInt, base: BigInt, digit: BigInt = 1): List[Char] = {
      if (BigInt(0) == number) Nil
      else {
        val remainder = number % (base)
        ALPHABET(remainder.toInt) :: divmod(number / base, base)
      }
    }

    override def apply(uuidGen: () => UUID = UUID.randomUUID) = {
      val uuidString = uuidGen().toString()
      divmod(BigInt(uuidString.replace("-", ""), 16), 64).mkString
    }
  }
}

