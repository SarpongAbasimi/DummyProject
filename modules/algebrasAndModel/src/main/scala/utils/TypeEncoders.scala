package utils

import cats.effect.Sync
import io.circe.Encoder
import io.circe.generic.extras.semiauto.deriveUnwrappedEncoder
import utils.Types.{Organization, PostSubscriptionData, PostSubscriptions, Repository}
import io.circe.generic.semiauto.deriveEncoder
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

object TypeEncoders {
  implicit val organizationEncoder: Encoder[Organization] = deriveUnwrappedEncoder[Organization]
  implicit val repositoryEncoder: Encoder[Repository]     = deriveUnwrappedEncoder[Repository]
  implicit val postSubscriptionsDataEncoder: Encoder[PostSubscriptionData] =
    deriveEncoder[PostSubscriptionData]

  implicit val postSubscriptionsEncoder: Encoder[PostSubscriptions] =
    deriveEncoder[PostSubscriptions]
  implicit def postSubscriptionsEntityEncoder[F[_]: Sync]: EntityEncoder[F, PostSubscriptions] =
    jsonEncoderOf[F, PostSubscriptions]
}
