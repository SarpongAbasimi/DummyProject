package models
import utils.Utils._

sealed trait Contributor

final case class RepositoryContributor(
    login: Login,
    id: Id,
    nodeId: NodeId,
    avatarUrl: AvatarUrl,
    gravatarId: GravatarId,
    url: Url,
    htmlUrl: HtmlUrl,
    followersUrl: FollowersUrl,
    followingUrl: FollowingUrl,
    gistsUrl: GistsUrl,
    starredUrl: StarredUrl,
    subscriptionsUrl: SubscriptionsUrl,
    organizationUrl: OrganizationUrl,
    reposUrl: ReposUrl,
    eventsUrl: EventsUrl,
    receivedEventsUrl: ReceivedEventsUrl,
    `type`: Type,
    siteAdmin: SiteAdmin,
    contributions: Contributions
) extends Contributor
