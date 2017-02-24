enablePlugins(JavaAppPackaging)

name := "docker-test"

version := "0.1.0"
dockerExecCommand := Seq("sudo", "docker")

packageName in Docker := "docker-package"

executableScriptName := "docker-exec"

maintainer := "Gary Coady <gary@lyranthe.org>"

TaskKey[Unit]("check-dockerfile") <<= (target, streams) map { (target, out) =>
  val dockerfile = IO.read(target / "docker" / "Dockerfile")
  assert(
    dockerfile.contains("ENTRYPOINT [\"bin/docker-exec\"]\n"),
    "dockerfile doesn't contain ENTRYPOINT [\"docker-exec\"]\n" + dockerfile
  )
  out.log.success("Successfully tested control script")
  ()
}
