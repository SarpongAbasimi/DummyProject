import cats.effect.Sync
import org.http4s.client.Client
import Entities._
import io.circe.Decoder._
import org.http4s.circe.CirceEntityCodec._
import ApplicationEncodersDecoders._

trait GitHubClient[F[_]] {
  def contributors(owner: String, repo: String)      : F[List[Contributors]]
}

object GitHubClient {
  def imp[F[_] : Sync](httpClient : Client[F]) = new GitHubClient[F] {
    def contributors(owner: String, repo: String) : F[List[Contributors]] = httpClient.expect[List[Contributors]](s"https://api.github.com/" +
      s"repos/${owner}/${repo}/contributors")
  }
}