package utils
import io.circe.generic.extras.Configuration
import io.circe.{Decoder, Encoder}
import io.circe.generic.extras.semiauto.{
  deriveConfiguredDecoder,
  deriveConfiguredEncoder,
  deriveUnwrappedDecoder,
  deriveUnwrappedEncoder
}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

sealed trait Subscription extends Product with Serializable

object Types {
  implicit val config: Configuration = Configuration.default.withSnakeCaseMemberNames

  final case class Login(login: String)                             extends AnyVal
  final case class Id(id: Int)                                      extends AnyVal
  final case class NodeId(nodeId: String)                           extends AnyVal
  final case class AvatarUrl(avatarUrl: String)                     extends AnyVal
  final case class GravatarId(gravatarId: String)                   extends AnyVal
  final case class Url(url: String)                                 extends AnyVal
  final case class HtmlUrl(htmlUrl: String)                         extends AnyVal
  final case class FollowersUrl(followersUrl: String)               extends AnyVal
  final case class FollowingUrl(followingUrl: String)               extends AnyVal
  final case class GistsUrl(gitUrl: String)                         extends AnyVal
  final case class StarredUrl(starredUrl: String)                   extends AnyVal
  final case class SubscriptionsUrl(subscriptionsUrl: String)       extends AnyVal
  final case class OrganizationUrl(organizationUrl: String)         extends AnyVal
  final case class ReposUrl(reposUrl: String)                       extends AnyVal
  final case class EventsUrl(eventsUrl: String)                     extends AnyVal
  final case class ReceivedEventsUrl(receivedEventsUrl: String)     extends AnyVal
  final case class Type(`type`: String)                             extends AnyVal
  final case class SiteAdmin(siteAdmin: Boolean)                    extends AnyVal
  final case class Name(name: String)                               extends AnyVal
  final case class Company(company: Option[String])                 extends AnyVal
  final case class Blog(blog: Option[String])                       extends AnyVal
  final case class Location(location: Option[String])               extends AnyVal
  final case class Email(email: Option[String])                     extends AnyVal
  final case class Hireable(hireable: Option[Boolean])              extends AnyVal
  final case class Bio(bio: Option[String])                         extends AnyVal
  final case class TwitterUserName(twitterUserName: Option[String]) extends AnyVal
  final case class PublicRepo(publicRepo: Int)                      extends AnyVal
  final case class PublicGist(publicGist: Int)                      extends AnyVal
  final case class Followers(followers: Int)                        extends AnyVal
  final case class Following(following: Int)                        extends AnyVal
  final case class CreatedAt(createdAt: String)                     extends AnyVal
  final case class UpdatedAt(updatedAt: String)                     extends AnyVal
  final case class Tag(tag: String)                                 extends AnyVal
  final case class VersionName(versionName: String)                 extends AnyVal
  final case class Sha(sha: String)                                 extends AnyVal
  final case class ZipBallUrl(zipBallUrl: String)                   extends AnyVal
  final case class TarballUrl(tarBallUrl: String)                   extends AnyVal
  final case class Owner(owner: String)                             extends AnyVal
  final case class Repo(repo: String)                               extends AnyVal
  final case class Contributions(contributions: Int)                extends AnyVal
  final case class Organization(organization: String)               extends AnyVal
  object Organization {
    implicit val organizationEncoder: Encoder[Organization] = deriveUnwrappedEncoder[Organization]
    implicit val organizationDecoder: Decoder[Organization] = deriveUnwrappedDecoder[Organization]
  }
  final case class Repository(repository: String) extends AnyVal
  object Repository {
    implicit val repositoryDecoder: Decoder[Repository] = deriveUnwrappedDecoder[Repository]
    implicit val repositoryEncoder: Encoder[Repository] = deriveUnwrappedEncoder[Repository]
  }
  final case class SubscribeAt(subscribeAt: String) extends AnyVal
  final case class Commit(sha: Sha, url: Url)
  final case class GetSubscriptionData(
      organization: Organization,
      repository: Repository,
      subscribeAt: SubscribeAt
  )
  final case class PostSubscriptionData(organization: Organization, repository: Repository)
  object PostSubscriptionData {
    implicit val postSubscriptionsDataDecoder: Decoder[PostSubscriptionData] =
      deriveDecoder[PostSubscriptionData]
    implicit val postSubscriptionsDataEncoder: Encoder[PostSubscriptionData] =
      deriveEncoder[PostSubscriptionData]
  }
  final case class GetSubscriptions(subscriptions: List[GetSubscriptionData]) extends Subscription

  final case class PostSubscriptions(subscriptions: List[PostSubscriptionData]) extends Subscription
  object PostSubscriptions {
    implicit val postSubscriptionsDecoder: Decoder[PostSubscriptions] =
      deriveDecoder[PostSubscriptions]
    implicit val postSubscriptionsEncoder: Encoder[PostSubscriptions] =
      deriveEncoder[PostSubscriptions]
  }

  final case class Token(token: String) extends AnyVal
  object Token {
    implicit val encoder: Encoder[Token] = deriveUnwrappedEncoder[Token]
    implicit val decoder: Decoder[Token] = deriveUnwrappedDecoder[Token]
  }
  final case class TeamId(teamId: String) extends AnyVal
  object TeamId {
    implicit val encoder: Encoder[TeamId] = deriveUnwrappedEncoder[TeamId]
    implicit val decoder: Decoder[TeamId] = deriveUnwrappedDecoder[TeamId]
  }

  final case class TeamDomain(teamDomain: String) extends AnyVal
  object TeamDomain {
    implicit val encoder: Encoder[TeamDomain] = deriveUnwrappedEncoder[TeamDomain]
    implicit val decoder: Decoder[TeamDomain] = deriveUnwrappedDecoder[TeamDomain]
  }

  final case class EnterpriseId(enterpriseId: String) extends AnyVal
  object EnterpriseId {
    implicit val encoder: Encoder[EnterpriseId] = deriveUnwrappedEncoder[EnterpriseId]
    implicit val decoder: Decoder[EnterpriseId] = deriveUnwrappedDecoder[EnterpriseId]
  }

  final case class EnterpriseName(enterpriseName: String) extends AnyVal
  object EnterpriseName {
    implicit val encoder: Encoder[EnterpriseName] =
      deriveUnwrappedEncoder[EnterpriseName]
    implicit val decoder: Decoder[EnterpriseName] =
      deriveUnwrappedDecoder[EnterpriseName]
  }

  final case class ChannelId(channelId: String) extends AnyVal
  object ChannelId {
    implicit val encoder: Encoder[ChannelId] = deriveUnwrappedEncoder[ChannelId]
    implicit val decoder: Decoder[ChannelId] = deriveUnwrappedDecoder[ChannelId]
  }

  final case class ChannelName(channelName: String) extends AnyVal
  object ChannelName {
    implicit val encoder: Encoder[ChannelName] = deriveUnwrappedEncoder[ChannelName]
    implicit val decoder: Decoder[ChannelName] = deriveUnwrappedDecoder[ChannelName]
  }
  final case class UserId(userId: String) extends AnyVal
  object UserId {
    implicit val encoder: Encoder[UserId] = deriveUnwrappedEncoder[UserId]
    implicit val decoder: Decoder[UserId] = deriveUnwrappedDecoder[UserId]
  }

  final case class UserName(userName: String) extends AnyVal
  object UserName {
    implicit val encoder: Encoder[UserName] = deriveUnwrappedEncoder[UserName]
    implicit val decoder: Decoder[UserName] = deriveUnwrappedDecoder[UserName]
  }

  final case class Command(command: String) extends AnyVal
  object Command {
    implicit val encoder: Encoder[Command] = deriveUnwrappedEncoder[Command]
    implicit val decoder: Decoder[Command] = deriveUnwrappedDecoder[Command]
  }

  final case class Text(text: String) extends AnyVal
  object Text {
    implicit val encoder: Encoder[Text] = deriveUnwrappedEncoder[Text]
    implicit val decoder: Decoder[Text] = deriveUnwrappedDecoder[Text]
  }

  final case class ResponseUrl(responseUrl: String) extends AnyVal
  object ResponseUrl {
    implicit val encoder: Encoder[ResponseUrl] = deriveUnwrappedEncoder[ResponseUrl]
    implicit val decoder: Decoder[ResponseUrl] = deriveUnwrappedDecoder[ResponseUrl]
  }

  final case class TriggerId(triggerId: String) extends AnyVal
  object TriggerId {
    implicit val encoder: Encoder[TriggerId] = deriveUnwrappedEncoder[TriggerId]
    implicit val decoder: Decoder[TriggerId] = deriveUnwrappedDecoder[TriggerId]
  }

  final case class ApiAppId(apiAppId: String) extends AnyVal
  object ApiAppId {
    implicit val encoder: Encoder[ApiAppId] = deriveUnwrappedEncoder[ApiAppId]
    implicit val decoder: Decoder[ApiAppId] = deriveUnwrappedDecoder[ApiAppId]
  }

  final case class SlackCommandRequestBody(
      token: Token,
      teamId: TeamId,
      teamDomain: TeamDomain,
      enterpriseId: EnterpriseId,
      enterpriseName: EnterpriseName,
      channelId: ChannelId,
      channelName: ChannelName,
      userId: UserId,
      userName: UserName,
      command: Command,
      text: Text,
      responseUrl: ResponseUrl,
      triggerId: TriggerId,
      apiAppId: ApiAppId
  ) extends Subscription

  object SlackCommandRequestBody {
    implicit val encoder: Encoder[SlackCommandRequestBody] =
      deriveConfiguredEncoder[SlackCommandRequestBody]
    implicit val decoder: Decoder[SlackCommandRequestBody] =
      deriveConfiguredDecoder[SlackCommandRequestBody]
  }
}
