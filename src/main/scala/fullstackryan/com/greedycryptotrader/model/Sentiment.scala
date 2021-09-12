package fullstackryan.com.greedycryptotrader.model

import io.circe.{Decoder, Encoder}

sealed abstract case class Sentiment(asString: String)
object ExtremeGreed extends Sentiment("ExtremeGreed")
object Greed        extends Sentiment("Greed")
object Neutral      extends Sentiment("Neutral")
object Fear         extends Sentiment("Fear")
object ExtremeFear  extends Sentiment("ExtremeFear")

object Sentiment {
  implicit val encoder: Encoder[Sentiment] = Encoder.encodeString.contramap[Sentiment] {
    case ExtremeGreed => ExtremeGreed.asString
    case Greed        => Greed.asString
    case Neutral      => Neutral.asString
    case Fear         => Fear.asString
    case ExtremeFear  => ExtremeFear.asString
  }

  implicit val decoder: Decoder[Sentiment] = Decoder.decodeString.emap {
    case ExtremeGreed.asString => Right(ExtremeGreed)
    case Greed.asString        => Right(Greed)
    case Neutral.asString      => Right(Neutral)
    case Fear.asString         => Right(Fear)
    case ExtremeFear.asString  => Right(ExtremeFear)
    case fail                  => Left(s"Could not decode Sentiment: $fail")
  }

}
