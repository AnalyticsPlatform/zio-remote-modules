val scala3Version = "3.2.0"

///////////////////////////////////////////////////////
lazy val `zio-client` = project
  .in(file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(
    run / fork := true,
    name := "akka-remote-modules-client",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies ++= {
      val vAkkaActorClient = "2.6.20"
      val vScallop = "4.1.0"
      val vIOCirce = "0.14.1"
      Seq(
        "org.scalameta" %% "munit" % "0.7.29" % Test,
        "com.typesafe.akka" %% "akka-actor" % vAkkaActorClient,
        "com.typesafe.akka" %% "akka-remote" % vAkkaActorClient,
        "io.netty" % "netty" % "3.10.6.Final",
        "io.circe" %% "circe-parser" % vIOCirce,
        "io.circe" %% "circe-generic" % vIOCirce,
        "org.rogach" %% "scallop" % vScallop
      )
    }
  )


///////////////////////////////////////////////////////

lazy val `serverScala2_12` = project
  .in(file("module-scala2_12"))
  .enablePlugins(JavaAppPackaging)
  .settings(
    run / fork := true,
    name := "module-scala2_12",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := "2.12.15",

    libraryDependencies ++= {
      val vAkkaActorModuleScala2_12 = "2.6.18"
      val vIOCirce = "0.14.1"
      val vScallop = "3.4.0"
      Seq(
        //      "org.scalameta" %% "munit" % "0.7.29" % Test,
        "com.typesafe.akka" %% "akka-actor" % vAkkaActorModuleScala2_12,
        "com.typesafe.akka" %% "akka-remote" % vAkkaActorModuleScala2_12,
        "io.netty" % "netty" % "3.10.6.Final",
        "io.circe" %% "circe-parser" % vIOCirce,
        "io.circe" %% "circe-generic" % vIOCirce,
        "org.rogach" %% "scallop" % vScallop
      )
    }
  )



///////////////////////////////////////////////////////

///////////////////////////////////////////////////////

lazy val `serverScala3` = project
  .in(file("module-scala3"))
  .enablePlugins(JavaAppPackaging)
  .settings(
    run / fork := true,
    name := "module-scala3",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,

    libraryDependencies ++= {
      val vIOCirce = "0.14.1"
      val vScallop = "4.1.0"
      val vAkkaActorModuleScala3 = "2.6.20"
      Seq(
        //      "org.scalameta" %% "munit" % "0.7.29" % Test,
        "com.typesafe.akka" %% "akka-actor" % vAkkaActorModuleScala3,
        "com.typesafe.akka" %% "akka-remote" % vAkkaActorModuleScala3,
        "io.netty" % "netty" % "3.10.6.Final",
        "io.circe" %% "circe-parser" % vIOCirce,
        "io.circe" %% "circe-generic" % vIOCirce,
        "org.rogach" %% "scallop" % vScallop
      )
    }
  )

///////////////////////////////////////////////////////

///////////////////////////////////////////////////////

lazy val `serverScala2_11` = project
  .in(file("module-scala2_11"))
  .enablePlugins(JavaAppPackaging)
  .settings(
    run / fork := true,
    name := "module-scala2_11",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := "2.11.12",

    libraryDependencies ++= {
      val vAkkaActorModuleScala2_11 = "2.5.23"
      val vIOCirce = "0.12.0-M3"
      val vScallop = "3.4.0"
      Seq(
        //      "org.scalameta" %% "munit" % "0.7.29" % Test,
        "com.typesafe.akka" %% "akka-actor" % vAkkaActorModuleScala2_11,
        "com.typesafe.akka" %% "akka-remote" % vAkkaActorModuleScala2_11,
        "io.netty" % "netty" % "3.10.6.Final",
        "io.circe" %% "circe-parser" % vIOCirce,
        "io.circe" %% "circe-generic" % vIOCirce,
        "org.rogach" %% "scallop" % vScallop
      )
    }
  )

///////////////////////////////////////////////////////

lazy val stageAll = taskKey[Unit]("Stage all projects")
lazy val prePackArchive = taskKey[Unit]("Prepare project before making tar.gz")
lazy val packArchive = taskKey[Unit]("Making release tar.gz")
lazy val runApp = taskKey[Unit]("run app")
lazy val makeTarGZ = taskKey[Unit]("Pack target dist tar.gz")
lazy val runClientApp = taskKey[Unit]("run client app")

val projects_stage = ScopeFilter(inProjects(
  `zio-client`, `serverScala2_11`, `serverScala2_12`, `serverScala3`), inConfigurations(Universal))

stageAll := {
  stage.all(projects_stage).value
}

runApp := Def.sequential(stageAll, prePackArchive, runClientApp).value

packArchive := Def.sequential(stageAll, prePackArchive, makeTarGZ).value

prePackArchive := {
  implicit val log = streams.value.log
  val targetStageDir = (`zio-client` / baseDirectory).value / "target" / "universal" / "stage"

  copyStageModule(targetStageDir, (serverScala3 / baseDirectory).value / "target" / "universal" / "stage")
  copyStageModule(targetStageDir, (`serverScala2_11` / baseDirectory).value / "target" / "universal" / "stage")
  copyStageModule(targetStageDir, (`serverScala2_12` / baseDirectory).value / "target" / "universal" / "stage")
}

makeTarGZ := {
  //TO-DO
}

import sbt.internal.util.ManagedLogger

def copyStageModule(targetStageDir: File, sourceStageDir: File)(implicit log: ManagedLogger) = {
  val targetStageBin = targetStageDir / "bin"
  val targetStageLib = targetStageDir / "lib"

  val sourceStageLib = sourceStageDir / "lib"
  val sourceStageBin = sourceStageDir / "bin"
  log.info(s"Copying libs dir ${sourceStageLib.getAbsolutePath} to ${
    targetStageLib.getAbsolutePath
  }")
  IO.copyDirectory(sourceStageLib, targetStageLib)
  log.info(s"Copying bin dir ${sourceStageBin.getAbsolutePath} to ${
    targetStageBin.getAbsolutePath
  }")
  IO.copyDirectory(sourceStageBin, targetStageBin)
}

runClientApp := {
  implicit val log = streams.value.log
  val targetStageDir = (`zio-client` / baseDirectory).value / "target" / "universal" / "stage" / "bin"
  val res = runCmdNoWait(Some("akka-remote-modules-client.bat"), Some("akka-remote-modules-client"), targetStageDir )
  res.exitValue()
}

def runCmd(cmdWindows: String, cmdUnix: String, path: File)
          (implicit log: ManagedLogger): String = {
  import scala.sys.process._
  val isWindows: Boolean = System.getProperty("os.name").toLowerCase().contains("win")
  try {
    if (isWindows) {
      Process("cmd /c " + cmdWindows, path) !!
    } else {
      Process("bash -c " + cmdUnix, path) !!
    }
  } catch {
    case ex: Throwable =>
      log.error(s"Could not execute command ${
        if (isWindows) cmdWindows
        else cmdUnix
      } in path ${path.getAbsolutePath} directory\n [exception]: " + ex.getMessage)
      throw ex
  }
}

  def runCmdNoWait(cmdWindows: Option[String], cmdUnix: Option[String], path: File): scala.sys.process.Process = {
    import scala.sys.process._
    val isWindows: Boolean = System.getProperty("os.name").toLowerCase().contains("win")
    try {
      if (isWindows && cmdWindows.nonEmpty) {
        Process("cmd /c " + cmdWindows.get, path).run()
      } else {
        if (cmdUnix.nonEmpty) 
          Process("bash -c " + cmdUnix.get, path).run()
        else throw new Exception("No cmd is provided")
      }
    } catch {
      case ex: Throwable =>
        println(s"Error: Could not execute command ${
          if (isWindows && cmdWindows.nonEmpty) cmdWindows.get
          else if (cmdUnix.nonEmpty) cmdUnix.get else ""
        } in path ${path.getAbsolutePath} directory\n [exception]: " + ex.getMessage)
        throw ex
    }
  }



