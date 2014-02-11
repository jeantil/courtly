import sbt._

object Dependencies {

  object Runtime {
    val secure_social = "securesocial" %% "securesocial" % "2.1.2"
    val akka_core="com.typesafe.akka" %% "akka-actor" % "2.3.0-RC1"
    val akka_slf4j="com.typesafe.akka" %% "akka-slf4j" % "2.3.0-RC1"

    val dependencies = Seq(
     akka_core
     , akka_slf4j
    )
  }

  object Test {
    val scala_test = "org.scalatest" % "scalatest_2.10" % "2.0" % "test"
    val scala_check="org.scalacheck" %% "scalacheck" % "1.10.0" % "test"
    val mockito = "org.mockito" % "mockito-all" % "1.9.5" % "test"
    val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % "2.3.0-RC1" % "test"
    val dependencies = {
      Seq(
        mockito
        , akkaTestkit
        , scala_check
        , scala_test
      )
    }
  }

  val buildDependencies = Runtime.dependencies ++ Test.dependencies
  val testDependencies = Test.dependencies
  val runtimeDependencies = Runtime.dependencies
}
