package models
import utils.Types._

sealed trait GitHubTag extends Product with Serializable

final case class RepositoryTag(
    versionName: VersionName,
    commit: Commit,
    zipBallUrl: ZipBallUrl,
    tarballUrl: TarballUrl,
    nodeId: NodeId
) extends GitHubTag
