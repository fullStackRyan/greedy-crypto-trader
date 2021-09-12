
val Http4sVersion          = "0.23.1"
val CirceVersion           = "0.14.1"
val MunitVersion           = "0.7.27"
val LogbackVersion         = "1.2.5"
val MunitCatsEffectVersion = "1.0.5"
val CatsEffect             = "3.2.3"
val Ciris                  = "2.0.1"


ThisBuild / envFileName := "meta/dev.env"


lazy val root = (project in file("."))
  .settings(
    organization := "fullstackryan.com",
    name := "greedy-crypto-trader",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.6",
    libraryDependencies ++= Seq(
      "org.http4s"     %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s"     %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s"     %% "http4s-circe"        % Http4sVersion,
      "org.http4s"     %% "http4s-dsl"          % Http4sVersion,
      "io.circe"       %% "circe-generic"       % CirceVersion,
      "org.scalameta"  %% "munit"               % MunitVersion % Test,
      "org.typelevel"  %% "munit-cats-effect-2" % MunitCatsEffectVersion % Test,
      "ch.qos.logback" % "logback-classic"      % LogbackVersion,
      "is.cir"         %% "ciris"               % Ciris,
      "is.cir"         %% "ciris-circe"         % Ciris,
      "is.cir"         %% "ciris-refined"       % Ciris,
      "is.cir"         %% "ciris-squants"       % Ciris,
      "is.cir"         %% "ciris-enumeratum"    % Ciris,
      "org.scalameta"  %% "svm-subs"            % "20.2.0",
      "org.typelevel"  %% "cats-effect"         % CatsEffect,
      "org.typelevel"  %% "cats-effect-kernel"  % CatsEffect,
      "org.typelevel"  %% "cats-effect-std"     % CatsEffect
    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.13.0" cross CrossVersion.full),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1"),
    testFrameworks += new TestFramework("munit.Framework")
  )
