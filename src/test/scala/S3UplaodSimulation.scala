import io.gatling.core.Predef._
import io.gatling.core.feeder.FeederBuilderBase
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.util.Random

class S3UplaodSimulation extends Simulation {

  val bucket = "bucket02"
  val endpoint = "http://localhost:9000"
  val protocol: HttpProtocolBuilder = http.baseUrl(endpoint).shareConnections

  val users = 1
  val feeds: FeederBuilderBase[String] = (1 to users).map(_ => {
    Map(s"fileName" -> Random.alphanumeric.take(10).mkString)
  })
  val bytes = new Array[Byte](10)

  val scn: ScenarioBuilder = scenario("s3 uploads")
    .feed(feeds)
    .exec(
      http("s3 upload")
      .put(s"$endpoint/$bucket/#{fileName}")
      .body(ByteArrayBody(bytes = bytes))
      .check(status.is(200))
    )

  setUp(scn.inject(atOnceUsers(users)).protocols(protocol))
}
