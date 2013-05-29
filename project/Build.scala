import sbt._
import Keys._

object MinimumBuild extends Build {

  val appName         = "iron-cache-plugin"
  val pluginVersion      = "1.0-SNAPSHOT"
  val buildVersion    = "2.1.0"

  val baseSettings = Defaults.defaultSettings ++ Seq(
    scalaVersion := "2.10.0"
  )

  lazy val plugin = Project(appName, file("plugin")).settings(baseSettings: _*).settings(
    version := pluginVersion,
    organization := "com.github.tmwtmp100",
    libraryDependencies += "com.typesafe" %% "play-plugins-util" % buildVersion,
    libraryDependencies += "play" %% "play" % buildVersion % "provided"
  )

  lazy val sampleProject = play.Project("iron-cache-sample", pluginVersion, path = file("sample")).settings(baseSettings: _*).settings(

  )

  lazy val root = Project("root", base = file("."))
    .settings(baseSettings: _*)
    .settings(
    publishLocal := {},
    publish := {}
  ).aggregate(plugin, sampleProject)



}