name := """play-rest-embededmongo-functional-test"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

routesGenerator := InjectedRoutesGenerator

libraryDependencies ++= Seq(
  "org.reactivemongo" %% "play2-reactivemongo" % "0.11.12",
  "com.github.simplyscala" %% "scalatest-embedmongo" % "0.2.4" % "test",
  "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % "test"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"


scalacOptions in ThisBuild ++= Seq("-feature", "-language:postfixOps")

fork in run := false
