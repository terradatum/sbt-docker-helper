enablePlugins(JavaAppPackaging)

name := "docker-test"

version := "0.1.0"
dockerExecCommand := Seq("sudo", "docker")
// packageName := "docker-package" // sets the executable script, too
packageName in Docker := "docker-package"

maintainer := "Gary Coady <gary@lyranthe.org>"

TaskKey[Unit]("check-dockerfile") <<= (target, streams) map { (target, out) =>
  val dockerfile = IO.read(target / "docker" / "Dockerfile")
  assert(
    dockerfile.contains("ENTRYPOINT [\"bin/docker-test\"]\n"),
    "dockerfile doesn't contain ENTRYPOINT [\"docker-test\"]\n" + dockerfile
  )
  out.log.success("Successfully tested control script")
  ()
}
