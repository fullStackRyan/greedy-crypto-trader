package fullstackryan.com.greedycryptotrader.logic

import cats.MonadThrow
import cats.effect.kernel.Async
import cats.implicits.{catsSyntaxApplicativeError, toFunctorOps}
import fullstackryan.com.greedycryptotrader.config.{Config, GreedyCryptoEnvironment}
import fullstackryan.com.greedycryptotrader.model.Result
import fullstackryan.com.greedycryptotrader.model.Result.{Failure, Success}

object GreedyCryptoTraderProgram {

  def handle[F[_]: Async](config: Config): F[Result] =
    GreedyCryptoEnvironment.of[F](config).use { env =>
      implicit val cryptoClient: CryptoClient[F] = env.alternativeClient

      runProgram[F]
    }

  def runProgram[F[_]: MonadThrow: CryptoClient]: F[Result] = {

    val program: F[Success] = for {
      data <- CryptoClient[F].get
    } yield Success(data)

    toResult(program.widen[Result])
  }

  def toResult[F[_]: MonadThrow](program: F[Result]): F[Result] =
    program.recover { case e => Failure(e) }

}
