import NativePackagerHelper._

ThisBuild / organization := "io.ase"
ThisBuild / scalaVersion := "2.13.1"
ThisBuild / scalacOptions ++= Seq(
	"-deprecation",
	"-encoding", "UTF-8"
)

val akkaVersion = "2.6.3"
val logbackVersion = "1.2.3"
val scalaTestVersion = "3.1.0"
lazy val hangman = (project in file("."))
	.enablePlugins(JavaAppPackaging)
	.enablePlugins(UniversalPlugin)
	.settings(
		name       := "Akka-Hangman",
		version    := "1.0.0",
		libraryDependencies ++=	Seq (
			"com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
			"com.typesafe.akka" %% "akka-slf4j"       % akkaVersion,
			"ch.qos.logback"    %  "logback-classic"  % logbackVersion,
			"com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
			"org.scalactic"     %% "scalactic" % scalaTestVersion % Test,
			"org.scalatest"			%% "scalatest" % scalaTestVersion % Test
		)
	)

// For Universal packaging via sbt-native-packager.
Universal / mappings  ++= directory(sourceDirectory.value / "main" / "resources")