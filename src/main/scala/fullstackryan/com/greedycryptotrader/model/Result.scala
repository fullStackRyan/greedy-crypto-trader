package fullstackryan.com.greedycryptotrader.model

import io.circe.syntax._
import io.circe.{Encoder, Json}

sealed trait Result extends Product with Serializable

object Result {

  implicit val encoder: Encoder[Result] = Encoder.instance {
    case Success(cryptoData) => Json.obj("result" -> "success".asJson, "cryptoData" -> cryptoData.asJson)
    case Failure(throwable)  => Json.obj("result" -> "failure".asJson, "error"      -> throwable.getMessage.asJson)
  }

  final case class Success(cryptoData: String)   extends Result
  final case class Failure(throwable: Throwable) extends Result

}
