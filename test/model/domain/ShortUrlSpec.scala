package model.domain

import org.specs2.matcher.ShouldMatchers
import org.specs2.mutable.Specification
import model.command.ShortenUrl

class ShortUrlSpec extends Specification with ShouldMatchers {
  "ShortUrl" should {
    "be created from a ShortenUrl command" in {
      val urlToShorten: String = "http://www.google.com"
      val shortUrl = ShortUrl(urlToShorten)
      shortUrl.target === urlToShorten
    }
  }
}
