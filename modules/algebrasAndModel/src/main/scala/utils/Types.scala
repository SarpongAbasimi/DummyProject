package utils

object Types {
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
  final case class Repository(repository: String)                   extends AnyVal
  final case class SubscribeAt(subscribeAt: String)                 extends AnyVal
  final case class Commit(sha: Sha, url: Url)
  final case class GetSubscriptionData(
      organization: Organization,
      repository: Repository,
      subscribeAt: SubscribeAt
  )
  final case class PostSubscriptionData(organization: Organization, repository: Repository)
  final case class GetSubscriptions(subscriptions: List[GetSubscriptionData])
  final case class PostSubscriptions(subscriptions: List[PostSubscriptionData])
}
