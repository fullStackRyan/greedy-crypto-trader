package fullstackryan.com.greedycryptotrader.model

object Errors {

  final case class AlternativeClientError(message: String) extends Throwable {
    override def getMessage: String = message
  }

}
