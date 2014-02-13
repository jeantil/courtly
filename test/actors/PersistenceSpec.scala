package actors

import akka.testkit.{ ImplicitSender, DefaultTimeout, TestKit }
import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpecLike }
import akka.actor.ActorSystem
import org.apache.commons.io.FileUtils

abstract class PersistenceSpec(_system: ActorSystem) extends TestKit(_system) with DefaultTimeout with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {

}
trait Cleanup { this: PersistenceSpec ⇒
  import java.io.File
  val storageLocations = List(
    "akka.persistence.journal.leveldb.dir",
    "akka.persistence.journal.leveldb-shared.store.dir",
    "akka.persistence.snapshot-store.local.dir").map(s ⇒ new File(system.settings.config.getString(s)))

  def cleanUp {
    storageLocations.foreach(FileUtils.deleteDirectory)
  }
}
