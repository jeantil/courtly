package services.token

import scala.util.Random
import services.TokenGenerator
import model.Token

class RandomTokenGenerator(minLength: Int, maxLength: Int, randomGen: Random = Random) extends TokenGenerator {
  override def generateToken(value: String = "", length: Int = minLength): Token = {
    val bound = Math.min(length, maxLength)
    val tokenString = (0 until bound).foldLeft("") { (acc, v) => acc + TokenAlphabets.ALPHABET_62(randomGen.nextInt(62)) }
    Token(tokenString)
  }
}
object RandomTokenGenerator {
  import play.api.PlayException
  def apply()(implicit app: play.api.Application): RandomTokenGenerator = {
    val minTokenLength = app.configuration.getInt(MinLengthKey).getOrElse(throw new PlayException("Missing RandomTokenGenerator setting", s"$MinLengthKey is a mandatory setting for RandomTokenGenerator"))
    val maxTokenLength = app.configuration.getInt(MaxLengthKey).getOrElse(throw new PlayException("Missing RandomTokenGenerator setting", s"$MaxLengthKey is a mandatory setting for RandomTokenGenerator"))
    new RandomTokenGenerator(minTokenLength, maxTokenLength)
  }
  val MinLengthKey = "token.generator.minLength"
  val MaxLengthKey = "token.generator.maxLength"
}