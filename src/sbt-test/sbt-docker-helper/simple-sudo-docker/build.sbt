enablePlugins(JavaAppPackaging)

name := "simple-sudo-docker"

version := "0.1.0"
dockerExecCommand := Seq("sudo", "docker")

maintainer := "G. Richard Bellamy <rbellamy@terradatum.com>"

dockerExecCommand := Seq("sudo", "docker")
