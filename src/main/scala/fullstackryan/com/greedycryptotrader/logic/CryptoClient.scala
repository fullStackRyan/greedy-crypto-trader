package fullstackryan.com.greedycryptotrader.logic

import cats.effect.Resource
import cats.effect.kernel.{Async, Concurrent}
import cats.syntax.all._
import fullstackryan.com.greedycryptotrader.config.Config
import fullstackryan.com.greedycryptotrader.model.CryptoData
import fullstackryan.com.greedycryptotrader.model.Errors.AlternativeClientError
import io.circe.Json
import org.http4s._
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.circe.jsonDecoder
import org.http4s.client.Client

trait CryptoClient[F[_]] {
  def get: F[CryptoData]
}

object CryptoClient {
  def apply[F[_]](implicit cryptoClient: CryptoClient[F]): CryptoClient[F] = cryptoClient

  def buildResource[F[_]: Async](config: Config): Resource[F, CryptoClient[F]] =
    BlazeClientBuilder[F](config.clientEc).resource
      .map(client => buildInstance[F](client)(config))

  def buildInstance[F[_]: Concurrent](client: Client[F])(config: Config): CryptoClient[F] = new CryptoClient[F] {

    def maybeErrorBody(response: Response[F]): F[Option[Json]] = response.attemptAs[Json].toOption.value

    def adaptUnknownErrors(e: Throwable): Throwable =
      e match {
        case e: AlternativeClientError => e
        case e                         => toContentfulClientError(e)
      }

    def toContentfulClientError(e: Throwable): Throwable = AlternativeClientError(e.getMessage)

    override def get: F[CryptoData] =
      client
        .expectOr[CryptoData](
          Request[F](
            Method.GET,
            config.alternative.base
          )
        ) { response =>
          maybeErrorBody(response)
            .map(json => AlternativeClientError(s"Unexpected status: ${response.status}: Error Body: $json"))
        }
        .adaptError(adaptUnknownErrors(_))

  }

}
