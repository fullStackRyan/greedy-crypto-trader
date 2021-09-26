package fullstackryan.com.greedycryptotrader.model

import io.circe.Decoder

case class CryptoHighLowPrice(
    priceHigh: Double,
    priceLow: Double,
)

object CryptoHighLowPrice {

  implicit val cryptoDecoder: Decoder[CryptoHighLowPrice] = Decoder.instance { json =>
    val data = json.downArray
    for {
      priceHigh <- data.get[Double]("price_high")
      priceLow  <- data.get[Double]("price_low")
    } yield CryptoHighLowPrice(priceHigh, priceLow)
  }

}
