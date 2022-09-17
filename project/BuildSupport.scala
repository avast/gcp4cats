import Dependencies.Cats
import com.typesafe.sbt.site.SitePlugin.autoImport._
import mdoc.MdocPlugin.autoImport._
import microsites.MicrositesPlugin.autoImport._
import sbt.Keys._
import sbt._
import sbtunidoc.ScalaUnidocPlugin.autoImport._
import sbtdynver.DynVerPlugin.autoImport._

import scala.collection.immutable

object BuildSupport {
  object ScalaVersions {
    lazy val scala212 = "2.12.16"
    lazy val scala213 = "2.13.8"
    lazy val scala3 = "3.1.3"
    lazy val supportedScalaVersions = List(scala212, scala213, scala3)
  }

  lazy val micrositeSettings = Seq(
    micrositeName := "datadog4s",
    micrositeDescription := "Cats-friendly scala library for interacting with Google Cloud Platform",
    micrositeAuthor := "Tomas Herman",
    micrositeGithubOwner := "avast",
    micrositeGithubRepo := "gcp4cats",
    micrositeUrl := "https://avast.github.io",
    micrositeDocumentationUrl := "api/latest/com/avast/datadog4s/",
    micrositeBaseUrl := "/gcp4cats",
    micrositeFooterText := None,
    micrositeGitterChannel := false,
    micrositeTheme := "pattern",
    mdocIn := file("site") / "docs",
    mdocVariables := Map(
      "VERSION" -> {
        if (!isSnapshot.value) { version.value }
        else { previousStableVersion.value.getOrElse("latestVersion") }

      },
      "CE2_LATEST_VERSION" -> "0.14.0",
      "CE3_LATEST_VERSION" -> {
        if (!isSnapshot.value) { version.value }
        else { previousStableVersion.value.getOrElse("latestVersion") }
      },
      "CATS_VERSION" -> Cats.core.revision,
      "CATS_EFFECT_VERSION" -> Cats.effect.revision,
      "SCALA_3_VERSION" -> ScalaVersions.scala3
    ),
    mdocAutoDependency := false,
    micrositeDataDirectory := file("site"),
    ScalaUnidoc / siteSubdirName := "api/latest",
    addMappingsToSiteDir(
      ScalaUnidoc / packageDoc / mappings,
      ScalaUnidoc / siteSubdirName
    )
  )
}
