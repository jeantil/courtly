resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Typesafe repository snap" at "http://repo.typesafe.com/typesafe/snapshots/"

resolvers += Resolver.url("Typesafe Ivy Public Snapshots Repository", url("http://repo.typesafe.com/typesafe/snapshots/"))(Resolver.ivyStylePatterns)

resolvers += Resolver.url("Scalasbt Ivy Public Snapshots Repository", url("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-snapshots/"))(Resolver.ivyStylePatterns)

resolvers += Resolver.url("Scalasbt Ivy Public Repository", url("http://repo.scala-sbt.org/scalasbt/repo/"))(Resolver.ivyStylePatterns)

resolvers += "jgit-repo" at "http://download.eclipse.org/jgit/maven"
