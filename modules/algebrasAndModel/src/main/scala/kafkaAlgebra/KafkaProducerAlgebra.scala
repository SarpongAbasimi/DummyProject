package kafkaAlgebra

import fs2.Stream

trait KafkaProducerAlgebra[F[_], K, V] {
  def publish(key: K, event: V): Stream[F, Unit]
}
