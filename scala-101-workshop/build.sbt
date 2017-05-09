scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  "com.twitter" %% "finatra-http" % "2.10.0",
  "org.scalikejdbc" %% "scalikejdbc" % "2.5.2",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.h2database" % "h2" % "1.4.195"
)

