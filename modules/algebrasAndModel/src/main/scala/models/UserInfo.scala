package models
import utils.Types._

sealed trait Details extends Product with Serializable

final case class UserDetails(
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
    receivedEventsUrl: ReceivedEventsUrl,
    `type`: Type,
    siteAdmin: SiteAdmin,
    name: Name,
    company: Company,
    blog: Blog,
    location: Location,
    email: Email,
    hireable: Hireable,
    bio: Bio,
    twitterUserName: TwitterUserName,
    publicRepo: PublicRepo,
    publicGist: PublicGist,
    followers: Followers,
    following: Following,
    createdAt: CreatedAt,
    updatedAt: UpdatedAt
) extends Details

final case class OwnerDetails(owner: Owner, repo: Repo) extends Details
