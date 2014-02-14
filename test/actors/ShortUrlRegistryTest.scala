package actors

import akka.actor.ActorSystem
import akka.actor.ReceiveTimeout
import com.typesafe.config.ConfigFactory
import model.query._
import model.event._
import model.command._
import model.domain._
import org.slf4j.LoggerFactory
import scala.concurrent.duration._
import akka.actor.PoisonPill

class ShortUrlRegistryTest(_system: ActorSystem) extends PersistenceSpec(_system) with Cleanup {

  import akka.actor.ActorRef

  val logger = LoggerFactory.getLogger(classOf[ShortUrlRegistryTest])
  private val msgDefaultWait: FiniteDuration = 2000.millis

  def this() = this(ActorSystem("ShortUrlRegistryTest", ConfigFactory.load("test-actorsystem.conf")))

  override def afterAll {
    import akka.testkit.TestKit
    TestKit.shutdownActorSystem(system)
    cleanUp
  }

  val actorRef = system.actorOf(ShortUrlRegistry.props(), "UrlRegistry")

  "The ShortUrlRegistry" should {
    "create a new short url for a new url" in {
      //Given
      val expectedUrl: String = "http://newurl"
      //When
      actorRef ! ShortenUrl(expectedUrl)
      //Then
      expectMsgPF(msgDefaultWait) {
        case ShortUrlCreated(ShortUrl(url, _)) => true
      }
    }

    "not create a new short url for the same url" in {
      //Given
      val expectedUrl: String = "http://newurl"
      actorRef ! ShortenUrl(expectedUrl)
      val expected = receiveOne(msgDefaultWait)
      //When
      actorRef ! ShortenUrl(expectedUrl)
      val actual = receiveOne(msgDefaultWait)
      // Then
      actual should be(expected)
    }

    "not resolve a token which hasn't been created" in {
      //Given
      val token: String = "newurl"
      //When
      actorRef ! ResolveToken(token)
      val expected = receiveOne(msgDefaultWait)
      //Then
      expected should be(ShortUrlNotFound)
    }

    "not resolve a token which has been created" in {
      //Given
      val expectedUrl: String = "http://newurl"
      val shortUrl = shortUrlExistFor(expectedUrl)
      //When
      actorRef ! ResolveToken(shortUrl.token)
      val ShortUrlFound(expected) = receiveOne(msgDefaultWait)
      //Then
      expected should be(shortUrl)
    }

    "Recover its state upon restart" in {
      //Given
      val expectedUrl: String = "http://newurl"
      val tempActorRef = system.actorOf(ShortUrlRegistry.props(), "UrlRegistryTemp")
      val shortUrl = shortUrlExistFor(expectedUrl, tempActorRef)
      tempActorRef ! ReceiveTimeout //kill existing actor
      receiveOne(msgDefaultWait)
      watch(tempActorRef)
      tempActorRef ! PoisonPill
      expectTerminated(tempActorRef)
      val recoveredActorRef = system.actorOf(ShortUrlRegistry.props(), "UrlRegistryTemp")
      //When
      recoveredActorRef ! ResolveToken(shortUrl.token)
      val ShortUrlFound(expected) = receiveOne(msgDefaultWait)
      //Then
      expected should be(shortUrl)
    }
  }

  private def shortUrlExistFor(expectedUrl: String, actor: ActorRef = actorRef): ShortUrl = {
    actor ! ShortenUrl(expectedUrl)
    val shortUrlCreated: AnyRef = receiveOne(msgDefaultWait)
    shortUrlCreated match {
      case ShortUrlCreated(s) => s
    }
  }

}
