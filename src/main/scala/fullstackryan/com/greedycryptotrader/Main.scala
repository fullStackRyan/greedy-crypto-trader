package fullstackryan.com.greedycryptotrader

import cats.Applicative
import cats.effect.{ExitCode, IO, IOApp}
import fullstackryan.com.greedycryptotrader.config.Config
import fullstackryan.com.greedycryptotrader.logic.GreedyCryptoTraderProgram

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {

    val program: IO[Unit] =
      for {
        ec     <- IO.executionContext
        config <- Config.readFromEnvironment[IO](ec)
        result <- GreedyCryptoTraderProgram.handle[IO](config)
        _ = println(result)
      } yield ()

    program.attempt.flatMap {
      case Left(value) =>
        Applicative[IO].pure(println(s"Editor Alerts failed with error: ${value.getMessage}")).as(ExitCode.Error)
      case Right(value) =>
        Applicative[IO].pure(println(s"Finished running Editor Alerts app with results: $value")).as(ExitCode.Success)

    }
  }

}
