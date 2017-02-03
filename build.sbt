name := "Akka-Hangman"

version := "1.0.0"

scalaVersion := "2.12.1"

scalaVersion in ThisBuild := "2.12.1"

connectInput in run := true

libraryDependencies ++= {
	val akkaVersion = "2.4.16"
	val httpVersion = "10.0.3"
	Seq (
		"com.typesafe.akka" %% "akka-actor" % akkaVersion,
		"com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
		"org.scalatest"			%% "scalatest" % "3.0.1" % "test"
	)
}