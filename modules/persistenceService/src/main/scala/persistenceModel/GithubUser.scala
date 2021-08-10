package persistenceModel

import java.util.UUID

sealed trait GithubUser                               extends Product with Serializable
final case class Id(id: UUID)                         extends AnyVal
final case class SlackUserId(slackUserId: UUID)       extends AnyVal
final case class SlackChannelId(slackChannelId: UUID) extends AnyVal

final case class User(
    id: Id,
    slackUserId: SlackUserId,
    slackChannelId: SlackChannelId
) extends GithubUser
