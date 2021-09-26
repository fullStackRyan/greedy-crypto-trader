package fullstackryan.com.greedycryptotrader.config

import cats.effect.kernel.{Async, Resource}
import fullstackryan.com.greedycryptotrader.logic.{CoinPriceClient, CryptoClient}

final case class GreedyCryptoEnvironment[F[_]](
    alternativeClient: CryptoClient[F],
    coinPriceClient: CoinPriceClient[F],
)

object GreedyCryptoEnvironment {

  def of[F[_]: Async](config: Config): Resource[F, GreedyCryptoEnvironment[F]] =
    for {
      fetchCryptoData      <- CryptoClient.buildResource[F](config)
      fetchCryptoPriceData <- CoinPriceClient.buildResource[F](config)
    } yield GreedyCryptoEnvironment[F](fetchCryptoData, fetchCryptoPriceData)

}
