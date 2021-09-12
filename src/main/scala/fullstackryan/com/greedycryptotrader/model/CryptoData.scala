package fullstackryan.com.greedycryptotrader.model

import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec
import io.circe.syntax._
import io.circe.{Decoder, Encoder, Json}

case class CryptoData(value: String, valueClassification: Sentiment)

object CryptoData {

//  implicit val encoder: Encoder[CryptoData] = io.circe.generic.semiauto.deriveEncoder

  implicit val cryptoDecoder: Decoder[CryptoData] = Decoder.instance { json =>
    val data = json.downField("data").downArray
    for {
      value <- data.get[String]("value")
      valueClassification <- data.get[Sentiment]("value_classification")
    } yield CryptoData(value, valueClassification)
  }

  implicit val encoder: Encoder[CryptoData] = Encoder.instance {
    case CryptoData(value, valueClassification) =>
      Json.obj(
        "value" := value.asJson,
        "value_classification" := valueClassification.asJson,
      )
  }

}
