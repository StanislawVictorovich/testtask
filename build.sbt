import com.typesafe.sbt.packager.docker.{Cmd, ExecCmd}

name := "sparkTT"

version := "0.1"

ThisBuild / scalaVersion := "2.12.11"
ThisBuild / organization := "com.tt"

val sparkVersion = "3.0.0"

val common = (project in file("common"))
  .settings(
    name := "common",
    libraryDependencies ++= Dependencies.spark
  )

def dockerSettings = {
  Seq(
    mappings in Universal := {
      val universalMappings = (mappings in Universal).value
      val assemblyJar = (assembly in Compile).value

      // removing means filtering
      val filtered = universalMappings filter {
        case (_, name) =>  ! name.endsWith(".jar")
      }

      // add the assembly jar
      filtered ++ Seq(
        assemblyJar -> ("app/" + assemblyJar.getName),
        file("template.sh") -> "template.sh"
      )
    },
    dockerUpdateLatest := true, // add latest tag
    dockerBaseImage := "bde2020/spark-submit:3.0.0-hadoop3.2",
    dockerRepository := Some("tt"), // image dir
    dockerCommands := dockerCommands.value.filter {
      case ExecCmd("ENTRYPOINT", _) => false
      case _ => true
    } ++ Seq(ExecCmd("CMD", "/opt/docker/template.sh"))
  )
}

val visitLogGenerator = (project in file("visitLogGenerator"))
  .settings(
    name := "visit-log-generator",
    test in assembly := {},
    libraryDependencies ++= Dependencies.spark,
    artifactName := { (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
      artifact.name + "_" + sv.binary + "-" + sparkVersion + "_" + module.revision + "." + artifact.extension
    }
  )
  .enablePlugins(JavaAppPackaging, DockerPlugin)
  .settings(
    dockerSettings
  )
  .settings(
    dockerEnvVars ++= Map("SPARK_APPLICATION_MAIN_CLASS" -> "com.tt.generator.VisitLogGeneratorJob"),
  )
  .dependsOn(common)

val snapshotComposer = (project in file("snapshotComposer"))
  .settings(
    name := "snapshot-composer",
    test in assembly := {},
    libraryDependencies ++= Dependencies.spark ++ Dependencies.allTests,
    artifactName := { (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
      artifact.name + "_" + sv.binary + "-" + sparkVersion + "_" + module.revision + "." + artifact.extension
    }
  )
  .enablePlugins(JavaAppPackaging, DockerPlugin)
  .settings(
    dockerSettings
  )
  .settings(
    dockerEnvVars ++= Map("SPARK_APPLICATION_MAIN_CLASS" -> "com.tt.composer.SnapshotComposerJob"),
  )
  .dependsOn(common)

val snapshotMerger = (project in file("snapshotMerger"))
  .settings(
    name := "snapshot-merger",
    test in assembly := {},
    libraryDependencies ++= Dependencies.spark ++ Dependencies.allTests,
    artifactName := { (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
      artifact.name + "_" + sv.binary + "-" + sparkVersion + "_" + module.revision + "." + artifact.extension
    }
  )
  .enablePlugins(JavaAppPackaging, DockerPlugin)
  .settings(
    dockerSettings
  )
  .settings(
    dockerEnvVars ++= Map("SPARK_APPLICATION_MAIN_CLASS" -> "com.tt.merger.SnapshotMergerJob"),
  )
  .dependsOn(common)

val snapshotToStorage = (project in file("snapshotToStorage"))
  .settings(
    name := "snapshot-to-storage",
    test in assembly := {},
    libraryDependencies ++= Dependencies.spark ++ Dependencies.allTests ++ Dependencies.db,
    artifactName := { (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
      artifact.name + "_" + sv.binary + "-" + sparkVersion + "_" + module.revision + "." + artifact.extension
    }
  )
  .enablePlugins(JavaAppPackaging, DockerPlugin)
  .settings(
    dockerSettings
  )
  .settings(
    dockerEnvVars ++= Map("SPARK_APPLICATION_MAIN_CLASS" -> "com.tt.storage.SnapshotToStorageJob"),
  )
  .dependsOn(common)

val snapshotAnalyzer = (project in file("snapshotAnalyzer"))
  .settings(
    name := "snapshot-analyzer",
    libraryDependencies ++=
      Dependencies.commonTests ++
        Dependencies.db ++
        Dependencies.slick ++
        Dependencies.logging ++
        Dependencies.slickTest
  )
  .dependsOn(common)