name := """html-validator"""
organization := "paulo.sims"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.3"

libraryDependencies += ws
libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies += "org.jsoup" % "jsoup" % "1.9.1"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "paulo.sims.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "paulo.sims.binders._"
