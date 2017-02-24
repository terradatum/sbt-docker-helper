enablePlugins(JavaAppPackaging)

name := "simple-docker"

version := "0.1.0"
dockerExecCommand := Seq("sudo", "docker")

maintainer := "Gary Coady <gary@lyranthe.org>"
