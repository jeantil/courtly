import sbt._
import sbt.Keys._

object Publish {
  import ohnosequences.sbt.SbtS3Resolver._
  import com.typesafe.sbt.packager.universal.Keys._

  val release = settingKey[Boolean]("is release")
  val gitHeadCommitSha = settingKey[String]("current git commit SHA")

  val settings = Seq(
    release := sys.props("release") == "true",
    gitHeadCommitSha in ThisBuild := sbt.Process("git rev-parse HEAD").lines.head,
    version in ThisBuild := {s"${gitHeadCommitSha.value}"},
    publishMavenStyle := false,
    publishTo in ThisBuild <<= (s3credentials) { (credentials) =>
        val prefix =  "snapshots"
        // if credentials are None, publishTo is also None
        credentials map S3Resolver(
          s"OpenOox $prefix S3 bucket"
          , s"s3://$prefix.openoox"
          , Resolver.ivyStylePatterns
        ).toSbtResolver
    }
  ) ++ com.typesafe.sbt.SbtNativePackager.deploymentSettings
}
