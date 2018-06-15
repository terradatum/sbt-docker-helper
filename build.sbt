import sbt.ScriptedPlugin._

sbtPlugin := true

lazy val `sbt-docker-helper` = (project in file(".")).
  enablePlugins(ScriptedPlugin).
  settings(
    organization := "com.terradatum",
    name := "sbt-docker-helper",
    description := "sbt-native-packager Docker plugin helper",
    version := "0.8.0-SNAPSHOT",
    scalaVersion := "2.12.6",
    addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.4"),
    // scripted test settings
    scriptedBufferLog := false,
    scriptedLaunchOpts := { scriptedLaunchOpts.value ++
      Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    },

    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "org.scalatest" %% "scalatest" % "3.0.5" % "test"
    ),

    mainClass in(Compile, run) := Some("com.terradatum.sbt.docker.Main"),
    publishMavenStyle := true
  )
