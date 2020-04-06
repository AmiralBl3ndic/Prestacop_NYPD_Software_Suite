import java.util.Properties
import org.apache.kafka.clients.producer._

import location.Location

/**
 * Wrapper around a Kafka producer following the Singleton design pattern
 */
trait KafkaProducerTrait {
  val bootstrapServers = "163.172.191.74:9092"
  val keySerializer = "org.apache.kafka.common.serialization.IntegerSerializer"
  val valueSerializer = "location.LocationSerializer"

  var locationProducerInstance: KafkaProducer[Integer, Location] = _

  /**
   * Get the a `KafkaProducer[String, String]` following Singleton design pattern
   * @return Instance of `KafkaProducer[String, String]`
   */
  val locationProducer: KafkaProducer[Integer, Location] = {
    if (locationProducerInstance != null) {
      locationProducerInstance
    } else {
      val props = new Properties()
      props.put("bootstrap.servers", bootstrapServers)
      props.put("key.serializer", keySerializer)
      props.put("value.serializer", valueSerializer)
      this.locationProducerInstance = new KafkaProducer[Integer, Location](props)
      locationProducerInstance
    }
  }
}
