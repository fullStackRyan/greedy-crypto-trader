package fullstackryan.com.greedycryptotrader.config

import cats.effect.Async
import ciris._
import org.http4s.Uri
import org.http4s.implicits.http4sLiteralsSyntax
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{DurationInt, FiniteDuration}

final case class Config(
    env: String,
    clientEc: ExecutionContext,
    alternative: AlternativeConfig,
    coinPrice: CoinPriceConfig,
)

final case class AlternativeConfig(
    base: Uri,
    retriesPerRequest: Int,
    initialRetryDelay: FiniteDuration
)

final case class CoinPriceConfig(
    base: Uri,
    retriesPerRequest: Int,
    initialRetryDelay: FiniteDuration,
    apiKey: Secret[String]
)

object Config {

  def readFromEnvironment[F[_]: Async](ec: ExecutionContext): F[Config] =
    (
      for {
        environment <- env("ENV")
        apiKey      <- env("COIN_API_KEY").secret
      } yield
        Config(
          environment,
          ec,
          AlternativeConfig(uri"https://api.alternative.me/fng/", retriesPerRequest = 3, initialRetryDelay = 1.seconds),
          CoinPriceConfig(
            uri"https://rest.coinapi.io/v1/ohlcv/BITSTAMP_SPOT_BTC_USD/history",
            retriesPerRequest = 3,
            initialRetryDelay = 1.seconds,
            apiKey
          )
        )
    ).load[F]

}
