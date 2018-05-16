lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "simple-sudo-docker",
    version := "0.2.0",
    scalaVersion := "2.12.6",
    dockerExecCommand := Seq("sudo", "docker"),
    maintainer := "G. Richard Bellamy <rbellamy@terradatum.com>"
  )