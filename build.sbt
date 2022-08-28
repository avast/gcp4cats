import Dependencies._

ThisBuild / scalaVersion := "2.13.8"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.example"
ThisBuild / organizationName := "example"

lazy val apiStorageModule = (project in file("code/api/storage")).settings(
  libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-effect" % "3.3.14",
    "co.fs2" %% "fs2-core" % "3.2.12",
    "com.google.cloud" % "google-cloud-storage" % "2.11.3"
  )
)

lazy val ce3CommonModule =
  (project in file("code/cats-effect3/common")).settings(
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % "3.3.14",
      "co.fs2" %% "fs2-core" % "3.2.12",
      "com.google.cloud" % "google-cloud-storage" % "2.11.3"
    )
  )

lazy val ce3StorageModule = (project in file("code/cats-effect3/storage"))
  .dependsOn(apiStorageModule, ce3CommonModule)
  .settings(
    libraryDependencies ++= Seq(
      "co.fs2" %% "fs2-io" % "3.2.12",
      "com.google.cloud" % "google-cloud-storage" % "2.11.3"
    )
  )

lazy val root = (project in file("."))
  .aggregate(apiStorageModule, ce3StorageModule)

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
