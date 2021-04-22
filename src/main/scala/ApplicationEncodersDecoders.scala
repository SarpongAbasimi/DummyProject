import Entities.{Contributors}
import cats.Applicative
import cats.effect.Sync
import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor}
import io.circe.generic.semiauto.deriveEncoder
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}

object ApplicationEncodersDecoders {

  implicit val contributorsDecoder: Decoder[Contributors] = new Decoder[Contributors] {
    override def apply(c: HCursor): Result[Contributors] = for {
      login             <- c.downField("login").as[String]
      id                <- c.downField("id").as[Int]
      nodeId            <- c.downField("node_id").as[String]
      avatarUrl         <- c.downField("avatar_url").as[String]
      gravatarId        <- c.downField("gravatar_id").as[String]
      url               <- c.downField("url").as[String]
      htmlUrl           <- c.downField("html_url").as[String]
      followersUrl      <- c.downField("followers_url").as[String]
      followingUrl      <- c.downField("following_url").as[String]
      gistsUrl          <- c.downField("gists_url").as[String]
      starredUrl        <- c.downField("starred_url").as[String]
      subscriptionsUrl  <- c.downField("subscriptions_url").as[String]
      organizationsUrl  <- c.downField("organizations_url").as[String]
      reposUrl          <- c.downField("repos_url").as[String]
      eventsUrl         <- c.downField("events_url").as[String]
      receivedEventsUrl <- c.downField("received_events_url").as[String]
      userType          <- c.downField("type").as[String]
      siteAdmin         <- c.downField("site_admin").as[Boolean]
      contributions     <- c.downField("contributions").as[Int]
    } yield Contributors(login, id, nodeId, avatarUrl, gravatarId, url, htmlUrl, followersUrl, followingUrl, gistsUrl,
      starredUrl, subscriptionsUrl, organizationsUrl, reposUrl, eventsUrl, receivedEventsUrl, userType, siteAdmin, contributions
    )
  }
  implicit def contributorsEntityDecoder[F[_]: Sync]: EntityDecoder[F, Contributors] = jsonOf
  implicit val contributorsEncoder: Encoder[Contributors] = deriveEncoder[Contributors]
  implicit def contributorsEntityEncoder[F[_]: Applicative]: EntityEncoder[F, List[Contributors]] = jsonEncoderOf
}