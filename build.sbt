name := "doobie-exercise"

organization := "com.github.tatatakky"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-effect"     % "2.1.3",
  "org.tpolecat"  %% "doobie-core"     % "0.8.8",
  "org.tpolecat"  %% "doobie-postgres" % "0.8.8"
)
