import com.typesafe.sbt.packager.universal.Keys._
import sbt._
import Keys._

object CourtlyBuild extends Build {
  val appName = "courtly"
  val appVersion = "1.0"
  val organization = "courtly"
  val stageConfig = ConfigKey("stage")

  import sbtbuildinfo.Plugin._
  import com.typesafe.sbt.packager.universal.Keys.{stage, dist}
  import com.tuplejump.sbt.yeoman.Yeoman._
  import sbt.Keys._

  val mygrunt =taskKey[Any]("run grunt")
  val appSettings: Seq[sbt.Setting[_]] =
    play.Project.playScalaSettings ++
    com.tuplejump.sbt.yeoman.Yeoman.yeomanSettings ++
    buildInfoSettings ++
    Seq(
      libraryDependencies ++=Dependencies.buildDependencies
      , resolvers := Repositories.resolvers
      , yeomanGruntfile := "Gruntfile.js"
      , yeomanDirectory <<= (baseDirectory in Compile) { _ / "ui"}
      , mygrunt := {
         val s = streams.value
          Process(s"grunt --gruntfile=${yeomanGruntfile.value} --force", yeomanDirectory.value) ! (s.log)
      }
      , testOptions in sbt.Test += Tests.Argument(TestFrameworks.Specs2, "junitxml")
      , stage <<= stage.dependsOn(mygrunt)
      , dist <<= dist.dependsOn(mygrunt)
      , play.Keys.playAssetsDirectories <+= baseDirectory / "ui/app/bower_components"
      , sourceGenerators in Compile <+= buildInfo
      , buildInfoKeys := Seq[BuildInfoKey](sbt.Keys.name, sbt.Keys.version, scalaVersion, sbtVersion)
      , incOptions := incOptions.value.withNameHashing(true)
      , buildInfoPackage := "io.courtly"
    ) ++ Format.settings
  val excludes = Seq("sbt-web","sbt-webdriver","sbt-js-engine")
  lazy val main = Project(appName,file(".")).settings(appSettings:_*).settings(
    libraryDependencies <<= libraryDependencies.apply { deps =>
      deps.map { dep => dep.exclude("com.typesafe","sbt-web").exclude("com.typesafe","sbt-webdriver").exclude("com.typesafe","sbt-js-engine")}
    }
  )

}
