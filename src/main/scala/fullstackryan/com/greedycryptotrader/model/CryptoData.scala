package fullstackryan.com.greedycryptotrader.model

import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec
import io.circe.syntax._
import io.circe.{Decoder, Encoder, Json}

import java.time.{Instant, ZoneId, ZonedDateTime}

case class CryptoData(value: String, valueClassification: Sentiment, timeStamp: ZonedDateTime)

object CryptoData {

  // Instant.ofEpochMilli is expecting milliseconds so we times it by 1000.
  def millisecondsToZDT(seconds: Long): ZonedDateTime =  ZonedDateTime.ofInstant(Instant.ofEpochMilli(seconds * 1000), ZoneId.systemDefault())


  implicit val cryptoDecoder: Decoder[CryptoData] = Decoder.instance { json =>
    val data = json.downField("data").downArray
    for {
      value               <- data.get[String]("value")
      valueClassification <- data.get[Sentiment]("value_classification")
      timestamp           <- data.get[String]("timestamp")
    } yield CryptoData(value, valueClassification, millisecondsToZDT(timestamp.toLong))
  }

  implicit val encoder: Encoder[CryptoData] = Encoder.instance {
    case CryptoData(value, valueClassification, timeStamp) =>
      Json.obj(
        "value" := value.asJson,
        "value_classification" := valueClassification.asJson,
        "timestamp" := timeStamp.asJson
      )
  }

}
