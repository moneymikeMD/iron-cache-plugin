import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "iron-cache-sample"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "com.github.tmwtmp100" %% "iron-cache-plugin" % "1.0"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    resolvers += "TMWTMP100 Repository" at "https://raw.github.com/tmwtmp100/maven/master/releases"
  )

}
