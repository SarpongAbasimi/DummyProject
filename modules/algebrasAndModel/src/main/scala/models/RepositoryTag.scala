package models
import utils.Utils._

sealed trait GitHubTag

final case class RepositoryTag(
    versionName: VersionName,
    commit: Commit,
    zipBallUrl: ZipBallUrl,
    tarballUrl: TarballUrl,
    nodeId: NodeId
) extends GitHubTag
