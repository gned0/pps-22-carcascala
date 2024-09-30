ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.2"

lazy val root = (project in file("."))
  .settings(
    name := "pps-22-carcascala",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % Test,
    libraryDependencies += "com.typesafe.play" %% "play-json" % "2.10.0-RC5",

      // ScalaFX related dependencies
    libraryDependencies += "org.scalafx" %% "scalafx" % "20.0.0-R31",
    libraryDependencies += "it.unibo.alice.tuprolog" % "tuprolog" % "3.3.0",

    // Scoverage settings
    coverageExcludedPackages := "carcassonne\\.view.*",

    libraryDependencies ++= {
      // Determine OS version of JavaFX binaries
      lazy val osName = System.getProperty("os.name") match {
        case n if n.startsWith("Linux") => "linux"
        case n if n.startsWith("Mac") => "mac"
        case n if n.startsWith("Windows") => "win"
        case _ => throw new Exception("Unknown platform!")
      }
      Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
        .map(m => "org.openjfx" % s"javafx-$m" % "16" classifier osName)
    },
    // META-INF discarding
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case x => MergeStrategy.first
    }
  )
