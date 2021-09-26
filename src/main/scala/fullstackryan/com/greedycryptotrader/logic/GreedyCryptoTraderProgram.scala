package fullstackryan.com.greedycryptotrader.logic

import cats.MonadThrow
import cats.effect.kernel.Async
import cats.syntax.all._
import fullstackryan.com.greedycryptotrader.config.{Config, GreedyCryptoEnvironment}
import fullstackryan.com.greedycryptotrader.model.Result
import fullstackryan.com.greedycryptotrader.model.Result.{Failure, Success}

import java.util.Date

object GreedyCryptoTraderProgram {

  def handle[F[_]: Async](config: Config): F[Result] =
    GreedyCryptoEnvironment.of[F](config).use { env =>
      implicit val cryptoClient: CryptoClient[F]       = env.alternativeClient
      implicit val coinPriceClient: CoinPriceClient[F] = env.coinPriceClient

      runProgram[F]
    }

  def runProgram[F[_]: MonadThrow: CryptoClient: CoinPriceClient]: F[Result] = {

    val program: F[Success] = for {
      cryptoData <- CryptoClient[F].get(1)
      fOfPrice = cryptoData.data.map(time => CoinPriceClient[F].getPrice(new Date(time.timeStamp))).sequence
      bitcoinPrice   <- fOfPrice // This seems messy!
      historicalData <- CryptoClient[F].get(0)
    } yield
      Success(s"your program successfully ran with: $cryptoData at a price of:$bitcoinPrice : ====> $historicalData")

    toResult(program.widen[Result])
  }

  def toResult[F[_]: MonadThrow](program: F[Result]): F[Result] =
    program.recover { case e => Failure(e) }

}
