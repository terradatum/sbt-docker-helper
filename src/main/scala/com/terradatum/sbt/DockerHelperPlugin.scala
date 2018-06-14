package com.terradatum.sbt
package packager
package docker

import com.typesafe.sbt.packager.docker.{DockerAlias, DockerPlugin}
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport._
import com.typesafe.sbt.packager.universal.UniversalPlugin.autoImport.stage
import sbt.Keys._
import sbt._
import sbt.Logger

// sbt.Process has been deprecated in 1.1.1, use the equivalent in Scala
import scala.sys.process.{Process, ProcessLogger}

/*
 * This helper plugin manipulates the behavior of the [[com.typesafe.sbt.packager.DockerPlugin]] in the following ways
 * 1. Modifies the [[dockerAlias]] to NOT include the [[dockerRepository]] by default. This allows us to publish local
 *    docker images in the form of <package name>:<package version> - this makes them more easily launched from the local
 *    docker daemon.
 * 2. This then means that the [[publish]] task must first tag the image to include the [[dockerRepository]] before doing
 *    the docker push.
 * 3. Adds a [[docker:clean]] task that will delete all images associated with this project.
 */
object DockerHelperPlugin extends AutoPlugin {

  override def requires: DockerPlugin.type = DockerPlugin

  override lazy val projectSettings: Seq[Def.Setting[_]] = inConfig(Docker)(dockerSettings)

  def dockerSettings = Seq(
    clean := cleanDockerTask.value,
    publishLocal := publishLocalDockerTask.value,
    tagDockerRepository := tagDockerRepositoryTask.value,
    publish := (publish dependsOn tagDockerRepository).value
  )

  lazy val clean: TaskKey[Unit] = taskKey[Unit]("Clean docker images from local Docker daemon store.") in Docker
  lazy val tagDockerRepository: TaskKey[Unit] = taskKey[Unit]("Tag the current image with the repository.") in Docker

  lazy val cleanDockerTask: Def.Initialize[Task[Unit]] = Def.task {
    val log: Logger = streams.value.log
    log.info("Cleaning Docker images")
    cleanDocker(dockerExecCommand.value, name.value, version.value, log)
  }

  /*
    Alters the default behavior of the the DockerPlugin so that the publishLocal task leaves off the dockerRepository
    value.
   */
  lazy val publishLocalDockerTask: Def.Initialize[Task[Unit]] = Def.task {
    val alias: DockerAlias = DockerAlias(None, None, name.value, Some(version.value))
    val buildOptions: Seq[String] = Seq("--force-rm") ++ Seq("-t", alias.versioned) ++ (
      if (dockerUpdateLatest.value)
        Seq("-t", dockerAlias.value.latest)
      else
        Seq()
      )
    val buildCommands: Seq[String] = dockerExecCommand.value ++ Seq("build") ++ buildOptions ++ Seq(".")
    DockerPlugin.publishLocalDocker(stage.value, buildCommands, streams.value.log)
  }

  /*
    Alters the default behavior of the DockerPlugin so that the publish task first tags the local image with the
    dockerRepository. Required since we altered the publishLocal task.
   */
  lazy val tagDockerRepositoryTask: Def.Initialize[Task[Unit]] = Def.task {
    publishLocal.value
    tagRepositoryDocker(dockerExecCommand.value, name.value, dockerRepository.value, (version in Docker).value, streams.value.log)
  }

  def cleanDocker(execCommand: Seq[String], imageName: String, imageVersion: String, log: Logger): Unit = {
    val listCommand = execCommand ++ Seq("images")
    log.debug(s"Executing ${listCommand.mkString(" ")}")
    val imageIds = (for {
      i <- Process(listCommand).!!.split("\n").
        map(_.split("\\s{2,}")) if i(0).indexOf(imageName) > -1
    } yield i(2)).distinct
    log.debug(s"imageId: ${imageIds.mkString(" ")}")
    if (!imageIds.isEmpty) {
      val cleanCommand = execCommand ++ Seq("rmi", "-f") ++ imageIds
      log.debug(s"Executing ${cleanCommand.mkString(" ")}")
      val ret = Process(cleanCommand) ! logger(log)

      if (ret != 0)
        throw new RuntimeException("Nonzero exit value: " + ret)
    }
  }

  def tagRepositoryDocker(execCommand: Seq[String], name: String, repository: Option[String], version: String, log: Logger): Unit = {
    for (repo <- repository) {
      val tagCommand = execCommand ++ Seq("tag", s"$name:$version", s"$repo/$name:$version")
      log.debug(s"Executing ${tagCommand.mkString(" ")}")
      val ret = Process(tagCommand) ! logger(log)

      if (ret != 0)
        throw new RuntimeException("Nonzero exit value: " + ret)
    }
  }

  // add methods to match signature of scala.sys.process.ProcessLogger
  private[this] def logger(log: Logger) = {
    new ProcessLogger {
      def err(err: => String) = {
        err match {
          case s if !s.trim.isEmpty => log.error(s)
          case s =>
        }
      }
      def out(output: => String) = output match{
        case s if !s.trim.isEmpty => log.info(s)
        case s =>
      }

      def info(inf: => String) = inf match {
        case s if !s.trim.isEmpty => log.info(s)
        case s =>
      }

      def buffer[T](f: => T) = f
    }
  }
}