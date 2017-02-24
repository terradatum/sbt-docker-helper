addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.2.0-M8")

// needs to be added for the docker spotify client
libraryDependencies += "com.spotify" % "docker-client" % "3.5.13"
