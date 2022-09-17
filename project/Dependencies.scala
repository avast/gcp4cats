import sbt._

object Dependencies {
  object Cats {
    val effect = "org.typelevel" %% "cats-effect" % "3.3.14"
    val core = "org.typelevel" %% "cats-core" % "2.7.0"
  }

  object FS2 {
    val core = "co.fs2" %% "fs2-core" % "3.2.14"
    val io = "co.fs2" %% "fs2-io" % "3.2.14"
  }

  object Testing {
    val mockitoScalatest = "org.mockito" %% "mockito-scala-scalatest" % "1.15.1"
    val munit = "org.scalameta" %% "munit" % "0.7.29"
  }

  object GCP {
    val storage = "com.google.cloud" % "google-cloud-storage" % "2.11.3"
  }

  object ScalaModules {
    val collectionCompat =
      "org.scala-lang.modules" %% "scala-collection-compat" % "2.8.1"
  }

  object Mdoc {
    val libMdoc = "org.scalameta" %% "mdoc" % "2.3.3" excludeAll (
      ExclusionRule(organization = "org.slf4j"),
      ExclusionRule(
        organization = "org.scala-lang.modules",
        name = "scala-collection-compat_2.13"
      )
    )
  }
}
