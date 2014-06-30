import sbt._
import Keys._
import play.Play.autoImport._
import PlayKeys._
    
object MinimumBuild extends Build {

  val appName         = "iron-cache-plugin"
  val pluginVersion   = "1.0"
  val buildVersion    = "2.3.0"

  val baseSettings = Defaults.defaultSettings ++ Seq(
    scalaVersion := "2.10.0"
  )
      
  val appDeps = Seq(
    ws,
    cache
  )

  lazy val plugin = Project(appName, file("plugin")).settings(
    version := pluginVersion,
    publishTo <<= version { (v: String) =>
      if (v.trim.endsWith("SNAPSHOT"))
        Some(Resolver.file("snapshots",  new File( "../maven-repo/snapshots" )))
      else
        Some(Resolver.file("releases",  new File( "../maven-repo/releases" )))
    },
//    publishTo := Some(Resolver.file("file",  new File( "path/to/my/maven-repo/releases" )) ),
    publishMavenStyle := true,
    organization := "com.github.tmwtmp100",
    libraryDependencies ++= Seq(cache % "provided", ws % "provided","com.typesafe.play" %% "play" % buildVersion % "provided")
  )

  lazy val sampleProject = Project("iron-cache-sample", file("sample")).enablePlugins(play.PlayScala).settings(
    publishLocal := {},
    publish := {},
    libraryDependencies ++= appDeps
  ).dependsOn(plugin % "compile->compile;test->test")

  lazy val root = Project("root", base = file(".")).enablePlugins(play.PlayScala).settings(
    publishLocal := {},
    publish := {}
  ).aggregate(plugin, sampleProject)



}