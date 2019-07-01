val ScalatraVersion = "2.6.5"

organization := "com.github.igneel64"

name := "JSON Schema Validator"

version := "1"

scalaVersion := "2.12.8"

fork in Test := true

resolvers += Classpaths.typesafeReleases

libraryDependencies ++= Seq(
  "org.scalatra" %% "scalatra" % ScalatraVersion,
  "org.scalatra" %% "scalatra-scalatest" % ScalatraVersion % "test",
  "ch.qos.logback" % "logback-classic" % "1.2.3" % "runtime",
  "org.eclipse.jetty" % "jetty-webapp" % "9.4.9.v20180320" % "compile;container",
  "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",
  "net.liftweb" %% "lift-json" % "3.1.1",
  "org.mongodb.scala" %% "mongo-scala-driver" % "2.6.0",
  "com.github.java-json-tools" % "json-schema-validator" % "2.2.10",
  "org.json4s" %% "json4s-jackson" % "3.6.7",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.9.9"
  )

enablePlugins(ScalatraPlugin)
enablePlugins(JavaAppPackaging)
