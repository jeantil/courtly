import sbt._

object Dependencies {

  object Runtime {
    val secure_social = "securesocial" %% "securesocial" % "2.1.2"
    val reactivemongo="org.reactivemongo" %% "play2-reactivemongo" % "0.10.2" exclude("org.apache.logging.log4j","log4j-core")

    val dependencies = Seq(
      reactivemongo
    )
  }

  object Test {
    val scala_test = "org.scalatest" % "scalatest_2.10" % "2.0" % "test"
    val scala_check="org.scalacheck" %% "scalacheck" % "1.10.0" % "test"
    val mockito = "org.mockito" % "mockito-all" % "1.9.5" % "test"
    val dependencies = {
      Seq(
        mockito
        , scala_check
        , scala_test
      )
    }
  }

  val buildDependencies = Runtime.dependencies ++ Test.dependencies
  val testDependencies = Test.dependencies
  val runtimeDependencies = Runtime.dependencies
}
