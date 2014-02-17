package actors

import akka.persistence.{ Snapshotter, EventsourcedProcessor }
import akka.actor.ActorLogging
import akka.actor.ReceiveTimeout
import akka.actor.PoisonPill
import akka.contrib.pattern.ShardRegion.Passivate
import concurrent.duration._
import model.domain.ShortUrl
import model.event._
import model.query.ReadTokenStats
import model.query.ResolveToken
import model.command.ShortenUrl
import play.api.libs.json.{ Json, JsValue }
import akka.persistence.SnapshotOffer
import akka.persistence.SaveSnapshotSuccess

class ShortUrlRegistry(receiveTimeout: Duration) extends EventsourcedProcessor with Snapshotter with ActorLogging {
  private var state = new PersistentState()
  type UrlCache = Map[String, ShortUrl]
  type TokenCache = Map[String, ShortUrl]

  case class PersistentState(_registeredUrls: UrlCache = Map(), _registeredTokens: TokenCache = Map()) {
    def registeredUrls = _registeredUrls
    def registeredTokens = _registeredTokens
    def registerShortUrl(shortUrl: ShortUrl): PersistentState = {
      copy(_registeredUrls = _registeredUrls.updated(shortUrl.target, shortUrl),
        _registeredTokens = _registeredTokens.updated(shortUrl.token, shortUrl))
    }
    def registerAccess(event: Event) = event match {
      case ShortUrlFound(shortUrl) => registerShortUrl(shortUrl.incrementAccessCount)
      case _                       => this
    }
  }
  object PersistentState {
    implicit val PersistentStateFormat = Json.format[PersistentState]
  }

  context.setReceiveTimeout(receiveTimeout)

  private var stopping = false

  override def receiveCommand: ShortUrlRegistry#Receive = {
    case c @ ShortenUrl(target) =>
      log.debug(s"received $c")
      state.registeredUrls.get(target) map (sender ! ShortUrlCreated(_)) getOrElse persist(ShortUrlCreated(ShortUrl(target)))({
        shortUrlCreated =>
          state = state.registerShortUrl(shortUrlCreated.shortUrl)
          sender ! shortUrlCreated
      })
    case q @ ResolveToken(token) =>
      log.debug(s"received $q")
      val event = state.registeredTokens.get(token) map ShortUrlFound getOrElse ShortUrlNotFound
      persist(event)(e => {
        state = state.registerAccess(e)
        sender ! e
      })
    case q @ ReadTokenStats(token) =>
      val event = state.registeredTokens.get(token) map (su => UrlStatFound(su.accessCount)) getOrElse UrlStatNotFound
      persist(event)(e => { sender ! event })
    case ReceiveTimeout =>
      stopping = true
      saveSnapshot(Json.toJson(state))
    case saved: SaveSnapshotSuccess =>
      if (stopping) context.parent ! Passivate(stopMessage = PoisonPill)
    case r => log.warning(s"received unknown message $r")
  }

  override def receiveRecover: ShortUrlRegistry#Receive = {
    case e @ ShortUrlCreated(shortUrl) =>
      log.info(s"Recovering state from event $e")
      state = state.registerShortUrl(shortUrl)
    case e @ ShortUrlFound(shortUrl) =>
      log.info(s"Recovering state from event $e")
      state = state.registerAccess(e)
    case s @ SnapshotOffer(metadata, snapshot: JsValue) =>
      log.info(s"Recovering state from snapshot $s")
      state = snapshot.validate[PersistentState].fold(
        errors => {
          log.error(s"Ignore invalid state snapshot $s ")
          state
        },
        newState => newState
      )
    case r => log.warning(s"receivedRecover ignored message $r")
  }
}

object ShortUrlRegistry {
  import akka.actor.Props
  def path = s"/user/$name"
  def name = "ShortUrlRegistry"
  def props(receiveTimeout: Duration = 10.minutes) = Props(new ShortUrlRegistry(receiveTimeout))
}
