scalaVersion := "2.11.0"

name := "scala-bullets"

organization := "com.tomergabel.examples"

scalacOptions ++= Seq( "-feature", "-Xfatal-warnings", "-deprecation" )

libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.7"