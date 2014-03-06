package services.token

import java.util.UUID
import services.TokenGenerator
import model.Token

class Base62UUIDTokenGenerator(uuidGen: () => UUID = UUID.randomUUID) extends TokenGenerator {
  override def generateToken(value: String = "", length: Int = 0): Token = {
    val uuidString = uuidGen().toString
    Token(divmod(BigInt(uuidString.replace("-", ""), 16), 62).mkString)
  }
  private def divmod(number: BigInt, base: BigInt, digit: BigInt = 1): List[Char] = {
    if (BigInt(0) == number) Nil
    else {
      val remainder = number % base
      TokenAlphabets.ALPHABET_62(remainder.toInt) :: divmod(number / base, base)
    }
  }
}

