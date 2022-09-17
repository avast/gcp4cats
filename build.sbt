import BuildSupport.ScalaVersions.*

ThisBuild / versionScheme := Some("early-semver")

lazy val mimaSettings = Seq(
  mimaPreviousArtifacts := previousStableVersion.value
    .map(organization.value %% name.value % _)
    .toSet
)

// settings only for projects that are published
lazy val publishSettings = Seq() ++ mimaSettings

lazy val scalaSettings = Seq(
  scalaVersion := scala3,
  scalacOptions := {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((3, _)) =>
        scalacOptions.value ++ Seq(
          "-source:future",
          "-language:adhocExtensions"
        )
      case Some((2, _)) => scalacOptions.value ++ Seq("-Xsource:3")
      case other        => scalacOptions.value
    }
  },
  crossScalaVersions := supportedScalaVersions,
  libraryDependencies ++= Seq(
    Dependencies.ScalaModules.collectionCompat,
    Dependencies.Testing.munit % Test
  ),
  testFrameworks += new TestFramework("munit.Framework")
)

lazy val commonSettings = Seq(
  sonatypeProfileName := "com.avast",
  organization := "com.avast.cloud",
  homepage := Some(url("https://github.com/avast/gcp4cats")),
  licenses := List(
    "MIT" -> url(
      s"https://github.com/avast/gcp4cats/blob/${version.value}/LICENSE"
    )
  ),
  description := "Cats-friendly library for working with Google Cloud Platform",
  developers := List(
    Developer(
      "tomasherman",
      "Tomas Herman",
      "hermant@avast.com",
      url("https://tomasherman.cz")
    )
  ),
  Test / publishArtifact := false,
  testOptions += Tests.Argument(TestFrameworks.JUnit)
)

lazy val apiStorageModule = (project in file("code/api/storage")).settings(
  name := "gcp4cats-api",
  commonSettings,
  scalaSettings,
  libraryDependencies ++= Seq(
    Dependencies.Cats.effect,
    Dependencies.FS2.io,
    Dependencies.GCP.storage
  )
)

lazy val ce3CommonModule =
  (project in file("code/cats-effect3/common")).settings(
    name := "gcp4cats-api",
    commonSettings,
    scalaSettings,
    libraryDependencies ++= Seq(
      Dependencies.Cats.effect,
      Dependencies.FS2.core,
      Dependencies.GCP.storage
    )
  )

lazy val ce3StorageModule = (project in file("code/cats-effect3/storage"))
  .dependsOn(apiStorageModule, ce3CommonModule)
  .settings(
    name := "gcp4cats-api",
    commonSettings,
    scalaSettings,
    libraryDependencies ++= Seq(
      Dependencies.Cats.effect,
      Dependencies.FS2.io,
      Dependencies.GCP.storage
    )
  )

lazy val global = project
  .in(file("."))
  .settings(name := "gcp4cats")
  .settings(mimaSettings)
  .settings(commonSettings)
  .settings(scalaSettings)
  .aggregate(ce3StorageModule, ce3CommonModule, apiStorageModule)
  .dependsOn(ce3StorageModule, ce3CommonModule, apiStorageModule)
  .disablePlugins(MimaPlugin)

addCommandAlias(
  "checkAll",
  "scalafmtSbtCheck; scalafmtCheckAll; +test; doc;" // site/makeMdoc"
)

addCommandAlias("fixAll", "; scalafmtSbt; scalafmtAll")
