#!groovy
import com.terradatum.jenkins.workflow.*

node {

  checkout scm

  echo "branch: ${env.BRANCH_NAME}"
  def pipeline = load "${pwd()}/release.groovy"

  if (env.BRANCH_NAME == 'master') {
    setDisplayName(pipeline.development() as Version)
  } else {
    setDisplayName(pipeline.other() as Version)
  }
}

def setDisplayName(Version version) {
  if (version) {
    currentBuild.displayName = version.toString()
  }
}