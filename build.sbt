name := "AkkaHttpSseSample"

version := "0.1"

scalaVersion := "2.12.3"

scalacOptions ++= Seq(
  "-target:jvm-1.8",
  "-unchecked",
  "-deprecation",
  "-Xcheckinit",
  "-encoding",
  "utf8",
  "-feature",
  "-Ywarn-unused-import"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.10"
)
