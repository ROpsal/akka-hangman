import NativePackagerHelper._

name := "Akka-Hangman"

version := "1.0.0"
scalaVersion := "2.12.6"

enablePlugins(JavaAppPackaging)

libraryDependencies ++= {
	val akkaVersion = "2.5.14"
	val logbackVersion = "1.2.3"
	val scalaTestVersion = "3.0.5"
	Seq (
		"com.typesafe.akka" %% "akka-actor"   % akkaVersion,
		"com.typesafe.akka" %% "akka-slf4j"   % akkaVersion,
		"ch.qos.logback" % "logback-classic"  % logbackVersion,
		"com.typesafe.akka" %% "akka-testkit" % akkaVersion      % "test",
		"org.scalactic"     %% "scalactic"    % scalaTestVersion % "test",
		"org.scalatest"			%% "scalatest"    % scalaTestVersion % "test"
	)
}

// For Universal packaging via sbt-native-packager.
mappings in Universal ++= directory(sourceDirectory.value / "main" / "resources")