scalaVersion := "2.12.2"

val finatra = "2.10.0"

libraryDependencies ++= Seq(
  "com.twitter" %% "finatra-http" % finatra,

  "com.twitter" %% "finatra-http" % finatra % "test",
  "com.twitter" %% "finatra-jackson" % finatra % "test",
  "com.twitter" %% "inject-server" % finatra % "test",
  "com.twitter" %% "inject-app" % finatra % "test",
  "com.twitter" %% "inject-core" % finatra % "test",
  "com.twitter" %% "inject-modules" % finatra % "test",
  "com.google.inject.extensions" % "guice-testlib" % "4.0" % "test",

  "com.twitter" %% "finatra-http" % finatra % "test" classifier "tests",
  "com.twitter" %% "finatra-jackson" % finatra % "test" classifier "tests",
  "com.twitter" %% "inject-server" % finatra % "test" classifier "tests",
  "com.twitter" %% "inject-app" % finatra % "test" classifier "tests",
  "com.twitter" %% "inject-core" % finatra % "test" classifier "tests",
  "com.twitter" %% "inject-modules" % finatra % "test" classifier "tests",

  "org.scalikejdbc" %% "scalikejdbc" % "2.5.2",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.h2database" % "h2" % "1.4.195",
  "org.scalaj" %% "scalaj-http" % "2.3.0" % "test",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.mockito" % "mockito-core" % "1.9.5" % "test"
)
