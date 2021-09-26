package fullstackryan.com.greedycryptotrader.logic

import cats.effect.Resource
import cats.effect.kernel.{Async, Concurrent}
import cats.syntax.all._
import fullstackryan.com.greedycryptotrader.config.Config
import fullstackryan.com.greedycryptotrader.model.{CryptoHighLowPrice, CryptoPrice}
import fullstackryan.com.greedycryptotrader.model.Errors.{AlternativeClientError, CoinApiClientError}
import io.circe.Json
import org.http4s._
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.circe.jsonDecoder
import org.http4s.client.Client

import java.text.SimpleDateFormat
import java.util.{Date, TimeZone}

trait CoinPriceClient[F[_]] {
  def getPrice(date: Date): F[CryptoPrice]
}

object CoinPriceClient {
  def apply[F[_]](implicit coinPriceClient: CoinPriceClient[F]): CoinPriceClient[F] = coinPriceClient

  def buildResource[F[_]: Async](config: Config): Resource[F, CoinPriceClient[F]] =
    BlazeClientBuilder[F](config.clientEc).resource
      .map(client => buildInstance[F](client)(config))

  def buildInstance[F[_]: Concurrent](client: Client[F])(config: Config): CoinPriceClient[F] = new CoinPriceClient[F] {

    def maybeErrorBody(response: Response[F]): F[Option[Json]] = response.attemptAs[Json].toOption.value

    def adaptUnknownErrors(e: Throwable): Throwable =
      e match {
        case e: CoinApiClientError => e
        case e                     => toCoinApiClientError(e)
      }

    def toCoinApiClientError(e: Throwable): Throwable = CoinApiClientError(e.getMessage)

    override def getPrice(date: Date): F[CryptoPrice] = {
      val tz = TimeZone.getTimeZone("UTC")
      val df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'") // Quoted "Z" to indicate UTC, no timezone offset
      df.setTimeZone(tz)
      val nowAsISO = df.format(date)

      val price: F[CryptoHighLowPrice] = client
        .expectOr[CryptoHighLowPrice](
          Request[F](
            Method.GET,
            config.coinPrice.base
              .withQueryParam("period_id", "1DAY")
              .withQueryParam("time_start", nowAsISO)
              .withQueryParam("apikey", config.coinPrice.apiKey.value)
          )
        ) { response =>
          maybeErrorBody(response)
            .map(json => AlternativeClientError(s"Unexpected status: ${response.status}: Error Body: $json"))
        }
        .adaptError(adaptUnknownErrors(_))

      price.map(prices => CryptoPrice((prices.priceHigh + prices.priceLow) / 2))
    }
  }

}
