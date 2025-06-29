import io.gatling.core.Predef._
import io.gatling.core.feeder.FeederBuilderBase
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import io.minio.http.Method
import io.minio.{GetPresignedObjectUrlArgs, MinioClient}

import java.util.concurrent.TimeUnit
import scala.util.Random


class PresignedUrlSimulation extends Simulation with MinioSupport {
  val endpoint = "http://localhost:9000"  // S3の場合、https://s3.amazonaws.com
  val accessKey = "AKIAIOSFODNN7EXAMPLE"
  val secretKey = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"
  val bucket = "bucket02"
  val protocol: HttpProtocolBuilder = http.baseUrl(endpoint).shareConnections

  val users = 10
  val feeds: FeederBuilderBase[String] = (1 to users).map(_ => {
    Map(s"objectName" -> Random.alphanumeric.take(10).mkString)
  })
  val bytes = new Array[Byte](10)

  val minioClient: MinioClient = getClient(endpoint, accessKey, secretKey)

  val scn: ScenarioBuilder = scenario("s3 uploads")
    .feed(feeds)
    .exec(
      http("s3 upload")
        .put(session => getPresignedUrl(minioClient, bucket, session("objectName").as[String]))
        .body(ByteArrayBody(bytes = bytes))
        .check(status.is(200))
    )

  setUp(scn.inject(atOnceUsers(users)).protocols(protocol))
}

trait MinioSupport {

  def getPresignedUrl(client: MinioClient, bucket: String, objectName: String): String =
    client.getPresignedObjectUrl(
      GetPresignedObjectUrlArgs.builder()
        .method(Method.PUT)
        .bucket(bucket)
        .`object`(objectName)
        .expiry(1, TimeUnit.DAYS)
        .build()
    )

  def getClient(endpoint: String, accessKey: String, secretKey: String): MinioClient =
      MinioClient.builder()
        .endpoint(endpoint)
        .credentials(accessKey, secretKey)
        .build()
}
