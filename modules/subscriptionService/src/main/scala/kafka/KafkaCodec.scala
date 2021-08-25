package kafka

import utils.OperationType
import utils.Types.{DeleteSubscription, MessageEvent, NewSubscription, Organization, Repository}
import vulcan.{AvroError, Codec}
import cats.implicits._

object KafkaCodec {
  implicit val operationTypeCodec: Codec[OperationType] = Codec.enumeration[OperationType](
    name = "operationType",
    namespace = "com.OperationType",
    symbols = List("NewSubscription", "DeleteSubscription"),
    encode = {
      case NewSubscription    => "NewSubscription"
      case DeleteSubscription => "DeleteSubscription"
    },
    decode = {
      case "NewSubscription"    => Right(NewSubscription)
      case "DeleteSubscription" => Right(DeleteSubscription)
      case other                => Left(AvroError(s"$other is not an operationType"))
    }
  )

  implicit val messageEventCodec: Codec[MessageEvent] = Codec.record[MessageEvent](
    name = "MessageEvent",
    namespace = "com.MessageEvent"
  ) { fields =>
    (
      fields("operationType", _.operationType),
      fields("organization", _.organization.organization),
      fields("respository", _.repository.repository)
    ).mapN((organizationType, organisation, repository) =>
      MessageEvent(
        organizationType,
        Organization(organisation),
        Repository(repository)
      )
    )
  }
}
