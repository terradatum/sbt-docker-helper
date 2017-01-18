#!/usr/bin/env groovy
import com.terradatum.jenkins.workflow.*

env.JRE_MAJOR = 8
env.JRE_UPDATE = 25

def project() {
  return 'terradatum/sbt-docker-help'
}

def branch() {
  return env.BRANCH_NAME
}

def jdkName() {
  return "jdk-1.${env.JRE_MAJOR}.0_${env.JRE_UPDATE}" as String
}

def sbtName() {
  return 'sbt-0.13.9'
}

def Version development() {

  stage 'checkout'
  gitCheckout {
    project = project()
    targetBranch = branch()
  }

  echo '...building DEV artifacts'

  Version versionWithBuildMetadata = cleanAndGetVersion()

  withBuildSbtSnapshot {
    projectPart = 'aergo'
    version = versionWithBuildMetadata
    cmds = {
      buildAndTest()
      publish()
    }
  }

  versionWithBuildMetadata
}

def void other() {

  stage 'checkout'
  gitCheckout {
    project = project()
    targetBranch = branch()
  }

  echo '...building OTHER'

  Version versionWithBuildMetadata = cleanAndGetVersion()

  withBuildSbtSnapshot {
    projectPart = 'aergo'
    version = versionWithBuildMetadata
    cmds = {
      buildAndTest()
    }
  }

  versionWithBuildMetadata
}

def Version cleanAndGetVersion() {
  stage 'clean'
  // better solution than clean - doesn't require loading sbt
  shell 'find . -name target -type d -prune -exec rm -rf {} +'

  stage 'versioning'
  Version projectVersion = getProjectVersion {
    projectType = ProjectType.Sbt
  }

  Version nextVersion = getNextVersion {
    project = "${project()}"
    version = projectVersion
  }

  Version versionWithBuildMetadata = getBuildMetadataVersion {
    version = nextVersion
  }
  versionWithBuildMetadata
}

def void buildAndTest() {
  stage 'sbt clean, build & test'
  withSbt {
    args = 'clean compile test scripted'
    jdkName = jdkName()
    sbtName = sbtName()
  }
}

def void publish() {
  stage 'sbt publish'
  withSbt {
    args = 'publish'
    jdkName = jdkName()
    sbtName = sbtName()
  }
}

return this;