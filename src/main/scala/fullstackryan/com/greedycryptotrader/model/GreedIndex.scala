package fullstackryan.com.greedycryptotrader.model

import io.circe.Decoder

case class GreedIndex(name: String, data: List[CryptoData])

object GreedIndex {

  implicit val greedDecoder: Decoder[GreedIndex] = Decoder.instance { json =>
    for {
      name <- json.get[String]("name")
      data <- json.get[List[CryptoData]]("data")
    } yield GreedIndex(name, data)
  }

}
