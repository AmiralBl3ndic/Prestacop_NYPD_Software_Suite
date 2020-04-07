package prestacop.kafka

import java.util.Properties

import org.apache.kafka.clients.producer.KafkaProducer
import prestacop.DroneData

/**
 * Wrapper around a Kafka producer following the Singleton design pattern
 */
trait KafkaProducerTrait {
  private val bootstrapServers = "163.172.191.74:9092"
  private val keySerializer = "org.apache.kafka.common.serialization.IntegerSerializer"
  private val valueSerializer = "prestacop.serialization.GenericSerializer"

  private val props = new Properties()
  props.put("bootstrap.servers", bootstrapServers)
  props.put("key.serializer", keySerializer)
  props.put("value.serializer", valueSerializer)

  var locationProducerInstance: KafkaProducer[Integer, DroneData] = _

  /**
   * Get the a `KafkaProducer[Integer, DroneData]` following Singleton design pattern
   * @return Instance of `KafkaProducer[Integer, DroneData]`
   */
  val locationProducer: KafkaProducer[Integer, DroneData] = {
    if (locationProducerInstance != null) {
      locationProducerInstance
    } else {
      this.locationProducerInstance = new KafkaProducer[Integer, DroneData](props)
      locationProducerInstance
    }
  }
}
