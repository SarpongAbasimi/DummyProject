package kafkaAlgebra
import fs2.Stream
import fs2.kafka.CommittableConsumerRecord

trait KafkaConsumerAlgebra[F[_], K, V] {
  def consume: Stream[F, CommittableConsumerRecord[F, K, V]]
}
