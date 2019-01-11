import NativePackagerHelper._

name := "Akka-Hangman"

version := "1.0.0"
scalaVersion := "2.12.8"

enablePlugins(JavaAppPackaging)

libraryDependencies ++= {
	val akkaVersion = "2.5.19"
	val logbackVersion = "1.2.3"
	val scalaTestVersion = "3.0.5"
	Seq (
		"com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
		"com.typesafe.akka" %% "akka-slf4j"       % akkaVersion,
		"ch.qos.logback"    %  "logback-classic"  % logbackVersion,
		"com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
		"org.scalactic"     %% "scalactic" % scalaTestVersion % Test,
		"org.scalatest"			%% "scalatest" % scalaTestVersion % Test
	)
}

// For Universal packaging via sbt-native-packager.
mappings in Universal ++= directory(sourceDirectory.value / "main" / "resources")