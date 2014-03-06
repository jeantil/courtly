package services

import model.Token

trait TokenGenerator {
  def generateToken(value: String = "", length: Int = 0): Token
}
