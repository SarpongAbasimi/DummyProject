package utils
import cats.effect.Sync
import io.circe.Decoder
import org.http4s.circe._
import utils.Types.{Organization, PostSubscriptionData, PostSubscriptions, Repository}
import org.http4s.EntityDecoder
import io.circe.generic.extras.semiauto.deriveUnwrappedDecoder
import io.circe.generic.semiauto.deriveDecoder

object TypeDecoders {
  implicit val organizationDecoder: Decoder[Organization] = deriveUnwrappedDecoder[Organization]
  implicit val repositoryDecoder: Decoder[Repository]     = deriveUnwrappedDecoder[Repository]
  implicit val postSubscriptionsDataDecoder: Decoder[PostSubscriptionData] =
    deriveDecoder[PostSubscriptionData]

  implicit val postSubscriptionsDecoder: Decoder[PostSubscriptions] =
    deriveDecoder[PostSubscriptions]

  implicit def postSubscriptionDataEntityDecoder[F[_]: Sync]: EntityDecoder[
    F,
    PostSubscriptionData
  ] =
    jsonOf[F, PostSubscriptionData]

  implicit def postSubscriptionsEntityDecoder[F[_]: Sync]: EntityDecoder[F, PostSubscriptions] =
    jsonOf[F, PostSubscriptions]
}
