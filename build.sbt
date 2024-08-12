ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.2"

lazy val root = (project in file("."))
  .settings(
    name := "pps-22-carcascala",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % Test,
  )
