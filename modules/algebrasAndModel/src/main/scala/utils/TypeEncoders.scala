package utils

import cats.effect.Sync
import io.circe.Encoder
import io.circe.generic.extras.semiauto.deriveUnwrappedEncoder
import utils.Types.{Organization, PostSubscriptionData, PostSubscriptions, Repository}
import io.circe.generic.semiauto.deriveEncoder
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

object TypeEncoders {
  implicit val organizationDecoder: Encoder[Organization] = deriveUnwrappedEncoder[Organization]
  implicit val repositoryDecoder: Encoder[Repository]     = deriveUnwrappedEncoder[Repository]
  implicit val postSubscriptionsDataDecoder: Encoder[PostSubscriptionData] =
    deriveEncoder[PostSubscriptionData]

  implicit val postSubscriptionsDecoder: Encoder[PostSubscriptions] =
    deriveEncoder[PostSubscriptions]

  implicit def postSubscriptionDataEntityDecoder[F[_]: Sync]: EntityEncoder[
    F,
    PostSubscriptionData
  ] =
    jsonEncoderOf[F, PostSubscriptionData]

  implicit def postSubscriptionsEntityDecoder[F[_]: Sync]: EntityEncoder[F, PostSubscriptions] =
    jsonEncoderOf[F, PostSubscriptions]
}
