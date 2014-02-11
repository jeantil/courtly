import sbt._

object Repositories{
  val byjean = "Byjean releases" at "http://repo.byjean.eu/releases/"
  val byjean_snapshots = "Byjean snapshots" at "http://repo.byjean.eu/snapshots/"
  val typesafe="Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/"
  val typesafe_snapshot="Typesafe repository snapshot" at "http://repo.typesafe.com/typesafe/snapshots/"
  val sonatype_snapshot="Sonatype Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
  val eligosource="Eligosource releases" at "http://repo.eligotech.com/nexus/content/repositories/eligosource-releases/"
  val eligosource_snapshot="Eligosource Snapshots" at "http://repo.eligotech.com/nexus/content/repositories/eligosource-snapshots/"
  val sbt_plugin_releases= Resolver.url("sbt-plugin-releases", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)

  val resolvers = Seq(
    byjean
    , byjean_snapshots
    , typesafe
    , typesafe_snapshot
    , sonatype_snapshot
    , eligosource_snapshot
    , sbt_plugin_releases
  )
}
