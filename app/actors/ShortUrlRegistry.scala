package actors

import akka.persistence.{ Snapshotter, EventsourcedProcessor }
import akka.actor.ActorLogging
import akka.actor.ReceiveTimeout
import akka.actor.PoisonPill
import akka.contrib.pattern.ShardRegion.Passivate
import concurrent.duration._
import model.domain.ShortUrl
import model.event._
import model.command.ShortenUrl
import model.query.ResolveToken
import play.api.libs.json.{ Json, JsValue }
import akka.persistence.SnapshotOffer
import akka.persistence.SaveSnapshotSuccess

class ShortUrlRegistry(receiveTimeout: Duration) extends EventsourcedProcessor with Snapshotter with ActorLogging {

  context.setReceiveTimeout(receiveTimeout)

  private var registeredUrls = Map[String, ShortUrl]()
  private var registeredTokens = Map[String, ShortUrl]()
  private var stopping = false
  private def registerShortUrl(shortUrl: ShortUrl) = {
    registeredUrls = registeredUrls.updated(shortUrl.target, shortUrl)
    registeredTokens = registeredTokens.updated(shortUrl.token, shortUrl)
  }

  override def receiveCommand: ShortUrlRegistry#Receive = {
    case c @ ShortenUrl(target) =>
      log.debug(s"received $c")
      registeredUrls.get(target) map (sender ! ShortUrlCreated(_)) getOrElse persist(ShortUrlCreated(ShortUrl(target)))({
        shortUrlCreated =>
          registerShortUrl(shortUrlCreated.shortUrl)
          sender ! shortUrlCreated
      })
    case c @ ResolveToken(token) =>
      log.debug(s"received $c")
      val event = registeredTokens.get(token) map ShortUrlFound getOrElse ShortUrlNotFound
      sender ! event
    case ReceiveTimeout =>
      stopping = true
      saveSnapshot(Json.obj("registeredUrls" -> registeredUrls, "registeredTokens" -> registeredTokens))
    case saved: SaveSnapshotSuccess =>
      if (stopping) context.parent ! Passivate(stopMessage = PoisonPill)
    case r => log.warning(s"received unknown message $r")
  }

  override def receiveRecover: ShortUrlRegistry#Receive = {
    case e @ ShortUrlCreated(shortUrl) =>
      log.info(s"Recovering state from event $e")
      registerShortUrl(shortUrl)
    case s @ SnapshotOffer(metadata, snapshot: JsValue) =>
      log.info(s"Recovering state from snapshot $s")
      registeredUrls = (snapshot \ "registeredUrls").as[Map[String, ShortUrl]]
      registeredTokens = (snapshot \ "registeredTokens").as[Map[String, ShortUrl]]
    case r => log.warning(s"receivedRecover unknown message $r")
  }
}
object ShortUrlRegistry {

  import akka.actor.Props

  def props(receiveTimeout: Duration = 10.minutes) = Props(new ShortUrlRegistry(receiveTimeout))
}
