package kafkaAlgebra

trait KafkaProducerAlgebra[F[_], K, V] {
  def publish(key: K, event: V): F[Unit]
}
