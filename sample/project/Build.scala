import sbt._
import Keys._
import play.Play.autoImport._
import PlayKeys._
    
object ApplicationBuild extends Build {

  val appName         = "iron-cache-sample"
  val appVersion      = "1.0-SNAPSHOT"

  val appDeps = Seq(
    ws,
    cache,
    "com.github.tmwtmp100" %% "iron-cache-plugin" % "1.0"
  )
  
  val main = Project(appName, file ("."), settings = Seq (libraryDependencies ++= appDeps)).enablePlugins(play.PlayScala).settings(
    resolvers += "TMWTMP100 Repository" at "https://raw.github.com/tmwtmp100/maven/master/releases"
  )

}
