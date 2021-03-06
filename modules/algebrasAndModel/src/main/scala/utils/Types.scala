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
import vulcan.Codec
import enumeratum.{Enum, EnumEntry, VulcanEnum}
import java.util.UUID
import java.time.Instant
import vulcan.generic._

object Types {
  implicit val config: Configuration = Configuration.default.withSnakeCaseMemberNames

  final case class Id(id: UUID)                         extends AnyVal
  final case class SlackUserId(slackUserId: String)     extends AnyVal
  final case class SlackChannelId(slackChannelId: UUID) extends AnyVal
  final case class Contributions(contributions: Int)    extends AnyVal
  final case class User(
      id: Id,
      slackUserId: SlackUserId,
      slackChannelId: SlackChannelId
  )

  final case class Owner(owner: String) extends AnyVal
  object Owner {
    implicit val encoder: Encoder[Owner] = deriveUnwrappedEncoder[Owner]
    implicit val decoder: Decoder[Owner] = deriveUnwrappedDecoder[Owner]
  }

  final case class RepositoryId(repositoryId: UUID) extends AnyVal
  object RepositoryId {
    implicit val decoder: Decoder[Repository] = deriveUnwrappedDecoder[Repository]
    implicit val encoder: Encoder[Repository] = deriveUnwrappedEncoder[Repository]
  }

  final case class Repository(repository: String) extends AnyVal
  object Repository {
    implicit val decoder: Decoder[Repository] = deriveUnwrappedDecoder[Repository]
    implicit val encoder: Encoder[Repository] = deriveUnwrappedEncoder[Repository]
    implicit val codec: Codec[Repository]     = Codec.derive[Repository]
  }

  final case class SubscribeAt(subscribeAt: Instant) extends AnyVal
  object SubscribeAt {
    implicit val decoder: Decoder[SubscribeAt] = deriveUnwrappedDecoder[SubscribeAt]
    implicit val encoder: Encoder[SubscribeAt] = deriveUnwrappedEncoder[SubscribeAt]
  }

  final case class GetSubscriptionData(
      owner: Owner,
      repository: Repository,
      subscribeAt: SubscribeAt
  )

  object GetSubscriptionData {
    implicit val encoder: Encoder[GetSubscriptionData] = deriveEncoder[GetSubscriptionData]
    implicit val decoder: Decoder[GetSubscriptionData] = deriveDecoder[GetSubscriptionData]
  }

  final case class PostSubscriptionData(
      organization: Owner,
      repository: Repository
  )
  final case class GetSubscriptions(subscriptions: List[GetSubscriptionData])

  object PostSubscriptionData {
    implicit val postSubscriptionsDataDecoder: Decoder[PostSubscriptionData] =
      deriveDecoder[PostSubscriptionData]
    implicit val postSubscriptionsDataEncoder: Encoder[PostSubscriptionData] =
      deriveEncoder[PostSubscriptionData]
  }
  final case class PostSubscriptions(subscriptions: List[PostSubscriptionData])
  object PostSubscriptions {
    implicit val decoder: Decoder[PostSubscriptions] =
      deriveDecoder[PostSubscriptions]
    implicit val encoder: Encoder[PostSubscriptions] =
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
  )

  object SlackCommandRequestBody {
    implicit val encoder: Encoder[SlackCommandRequestBody] =
      deriveConfiguredEncoder[SlackCommandRequestBody]
    implicit val decoder: Decoder[SlackCommandRequestBody] =
      deriveConfiguredDecoder[SlackCommandRequestBody]
  }

  final case class SchemaRegistryUrl(
      schemaRegistryUrl: String
  ) extends AnyVal

  final case class Password(
      password: String
  ) extends AnyVal

  final case class Topic(
      topic: String
  ) extends AnyVal
  final case class BootstrapServer(
      bootstrapServer: String
  ) extends AnyVal
  final case class GroupId(
      groupId: String
  ) extends AnyVal

  @AvroNamespace("com.OperationType")
  @AvroDoc("The different operation type")
  sealed trait OperationType extends Product with Serializable with EnumEntry
  object OperationType extends Enum[OperationType] with VulcanEnum[OperationType] {
    case object NewSubscription    extends OperationType
    case object DeleteSubscription extends OperationType

    val values = findValues
  }

  final case class Organization(organization: String) extends AnyVal

  object Organization {
    implicit val codec: Codec[Organization] = Codec.derive[Organization]
  }

  @AvroNamespace("com.MessageEvent")
  final case class MessageEvent(
      operationType: OperationType,
      organization: Organization,
      repository: Repository
  )

  object MessageEvent {
    implicit val codec: Codec[MessageEvent] =
      Codec.derive[MessageEvent]
  }
}
