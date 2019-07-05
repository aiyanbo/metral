import Dependencies._
import ReleaseTransformations._

name := "metral"

organization := "org.jmotor.metral"

scalaVersion := Versions.scala213

libraryDependencies ++= dependencies

enablePlugins(ProtocPlugin)

releaseCrossBuild := true

releasePublishArtifactsAction := PgpKeys.publishSigned.value

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  publishArtifacts,
  setNextVersion,
  commitNextVersion,
  pushChanges
)
