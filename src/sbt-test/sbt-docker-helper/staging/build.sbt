enablePlugins(DockerPlugin)

name := "simple-test"

version := "0.1.0"
dockerExecCommand := Seq("sudo", "docker")
