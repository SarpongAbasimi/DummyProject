package migrations
import cats.effect.{Sync}
import org.flywaydb.core.Flyway
import config.ApplicationConfig
import cats.implicits._
import org.flywaydb.core.api.output.MigrateResult

object DbMigrations {

  def migrate[F[_]: Sync](applicationConfig: ApplicationConfig): F[MigrateResult] =
    run[F](applicationConfig).flatMap(f => Sync[F].delay(f.migrate()))

  private def run[F[_]: Sync](applicationConfig: ApplicationConfig): F[Flyway] = {
    Sync[F].delay {
      Flyway
        .configure()
        .dataSource(
          applicationConfig.url.url,
          applicationConfig.user.user,
          applicationConfig.password.password
        )
        .load()
    }
  }
}
