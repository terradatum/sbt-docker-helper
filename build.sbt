import sbt.ScriptedPlugin._

sbtPlugin := true

lazy val `sbt-docker-helper` = (project in file(".")).
  settings(
    organization := "com.terradatum",
    name := "sbt-docker-helper",
    description := "sbt-native-packager Docker plugin helper",
    version := "0.4.0-SNAPSHOT",
    scalaVersion := "2.10.6",
    addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.2.0-SNAPSHOT"),
    // scripted test settings
    scriptedSettings,
    scriptedBufferLog := false,
    scriptedLaunchOpts ++= sys.process.javaVmArguments.filter(
      a => Seq(
        "-Xmx",
        "-Xms",
        "-XX",
        "-Dsbt.ivy.home",
        "-Dsbt.boot.directory",
        "-Dsbt.boot.properties",
        "-Dsbt.log.noformat").exists(a.startsWith)
    ),
    scriptedLaunchOpts ++= Seq(
      "-Dplugin.version=" + version.value
    ),
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % "1.1.3",
      "org.scalatest" %% "scalatest" % "2.2.4" % "test"
    ),

    mainClass in(Compile, run) := Some("com.terradatum.sbt.docker.Main"),
    publishMavenStyle := true
  )