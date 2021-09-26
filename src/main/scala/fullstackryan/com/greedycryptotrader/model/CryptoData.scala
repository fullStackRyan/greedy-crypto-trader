package fullstackryan.com.greedycryptotrader.model

import io.circe.Decoder

case class CryptoData(greedValue: String, valueClassification: Sentiment, timeStamp: Long)

object CryptoData {

  implicit val cryptoDecoder: Decoder[CryptoData] = Decoder.instance { json =>
//    val data = json.downField("data").downArray
    for {
      value               <- json.get[String]("value")
      valueClassification <- json.get[Sentiment]("value_classification")
      timestamp           <- json.get[String]("timestamp")
    } yield CryptoData(value, valueClassification, timestamp.toLong * 1000)
  }

}
