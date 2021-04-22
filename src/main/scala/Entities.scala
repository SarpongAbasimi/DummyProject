object Entities {

  sealed trait ApplicationEntity

  final case class Contributors(
                                 login: String,
                                 id: Int,
                                 nodeId: String,
                                 avatarUrl: String,
                                 gravatarID: String,
                                 url: String,
                                 htmlUrl: String,
                                 followerUrl: String,
                                 followingUrl: String,
                                 gistsUrl: String,
                                 starredUrl: String,
                                 subscriptionsUrl: String,
                                 organizationUrl: String,
                                 reposUrl: String,
                                 eventUrl: String,
                                 receivedEventsUrl: String,
                                 userType: String,
                                 siteAdmin: Boolean,
                                 contributions: Int
                               )
}