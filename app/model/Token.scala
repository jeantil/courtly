package model

import play.api.libs.json.Format
import play.api.libs.json.JsString
import play.api.libs.json.{ JsResult, JsValue }

case class Token(value: String) extends AnyVal {
  def length = value.length
}

object Token {
  implicit val tokenFormat = new Format[Token] {
    override def writes(o: Token): JsValue = JsString(o.value)

    override def reads(json: JsValue): JsResult[Token] = json.validate[String].map(Token.apply)
  }
}