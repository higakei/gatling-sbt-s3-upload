enablePlugins(GatlingPlugin)

scalaVersion := "2.13.8"

scalacOptions := Seq(
  "-encoding", "UTF-8", "-target:jvm-1.8", "-deprecation",
  "-feature", "-unchecked", "-language:implicitConversions", "-language:postfixOps")

val gatlingVersion = "3.8.4"
libraryDependencies += "io.gatling.highcharts" % "gatling-charts-highcharts" % gatlingVersion % "test,it"
libraryDependencies += "io.gatling"            % "gatling-test-framework"    % gatlingVersion % "test,it"
//libraryDependencies += "com.amazonaws" % "aws-java-sdk" % "1.12.370"
libraryDependencies += "io.minio" % "minio" % "8.4.6"

// Enterprise Cloud (https://cloud.gatling.io/) configuration reference: https://gatling.io/docs/gatling/reference/current/extensions/sbt_plugin/#working-with-gatling-enterprise-cloud
// Enterprise Self-Hosted configuration reference: https://gatling.io/docs/gatling/reference/current/extensions/sbt_plugin/#working-with-gatling-enterprise-self-hosted
