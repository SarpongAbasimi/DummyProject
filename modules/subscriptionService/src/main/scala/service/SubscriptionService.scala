package service

import Errors.UserNotFound
import cats.data.OptionT
import cats.effect.Sync
import userDbAlgebra.UserAlgebra
import subscriptionAlgebra.{SubscriptionAlgebra, SubscriptionServiceAlgebra}
import utils.Types.{GetSubscriptionData, MessageEvent, Organization, PostSubscriptions, Repository, SlackUserId, User}
import doobie.ConnectionIO
import doobie.implicits._
import doobie.util.transactor.Transactor
import cats.implicits._
import kafkaAlgebra.KafkaProducerAlgebra
import utils.Types.OperationType.DeleteSubscription
import fs2.Stream

object SubscriptionService {
  def implementation[F[_]: Sync](
      userAlgebra: UserAlgebra[ConnectionIO],
      subscriptionServiceAlgebra: SubscriptionServiceAlgebra[ConnectionIO],
      transactor: Transactor[F],
      kafkaProducer: KafkaProducerAlgebra[F, String, MessageEvent]
  ): SubscriptionAlgebra[F] = new SubscriptionAlgebra[F] {
    def getUserSubscriptions(slackUserId: SlackUserId): F[List[GetSubscriptionData]] = {
      for {
        optionOfAUser <- userAlgebra.findUser(slackUserId)
        user          <- optionOfAUser.liftTo[ConnectionIO](UserNotFound("User does not Exit"))
        subscription  <- subscriptionServiceAlgebra.get(user.id)
      } yield subscription
    }.transact(transactor)

    def postUserSubscriptions(slackUserId: SlackUserId, subscriptions: PostSubscriptions): F[Unit] =
      (for {
        optionOfAUser <- userAlgebra.findUser(slackUserId)
        user <- optionOfAUser.liftTo[ConnectionIO](
          UserNotFound(
            s"Invalid: User with ${slackUserId.slackUserId} does not exit"
          )
        )
        _ <- subscriptionServiceAlgebra.post(user.id, subscriptions)
      } yield ()).transact(transactor)

    def deleteUserSubscription(
        slackUserId: SlackUserId,
        subscriptions: PostSubscriptions
    ): F[Unit] = {
      dbOperation(slackUserId, subscriptions)
        .transact(transactor)
        .flatMap { user =>
          subscriptions.subscriptions.traverse_ { subs =>
            kafkaProducer.publish(
              user.id.id.toString,
              MessageEvent(DeleteSubscription, Organization(subs.organization.owner), subs.repository)
            )
          }
        }
    }

    def dbOperation(slackUserId: SlackUserId, subscriptions: PostSubscriptions): ConnectionIO[User] =
      for {
        optionOfAUser <- userAlgebra.findUser(slackUserId)
        user <- optionOfAUser.liftTo[ConnectionIO](
          UserNotFound(s"Invalid: User with ${slackUserId.slackUserId} does not exit")
        )
        _ <- subscriptionServiceAlgebra.delete(user.id, subscriptions)
      } yield user

    def dbOperationT(slackUserId: SlackUserId, subscriptions: PostSubscriptions): ConnectionIO[User] =
      OptionT(userAlgebra.findUser(slackUserId))
        .semiflatTap(user => subscriptionServiceAlgebra.delete(user.id, subscriptions))
        .getOrElseF(UserNotFound(s"Invalid: User with ${slackUserId.slackUserId} does not exit").raiseError[ConnectionIO, User])

    def test: F[Option[String]] = {
      case class Person(age: Option[Int])
      val a: F[Option[Person]] = ???
      def b(i: Int): F[Boolean] = ???
      def c(b: Boolean): F[Option[String]] = ???

      val r: OptionT[F, String] = for {
        r1 <- OptionT(a).mapFilter(_.age)
        r2 <- OptionT.liftF(b(r1))
        r3 <- OptionT(c(r2))
      } yield r3

      r.value
    }
  }
}
