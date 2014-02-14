package model.event

import model.domain.ShortUrl
import com.google.common.eventbus.EventBus

case class ShortUrlCreated(shortUrl: ShortUrl) extends Event
case class ShortUrlFound(shortUrl: ShortUrl) extends Event
case object ShortUrlNotFound extends Event