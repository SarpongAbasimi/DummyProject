package kafkaAlgebra

import fs2.Stream

trait KafkaProducerAlgebra[F[_], K, V] {

  def publish(key: K, messageEvent: V): Stream[F, Unit]
}
