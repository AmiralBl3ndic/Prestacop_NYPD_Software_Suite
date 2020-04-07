package prestacop.kafka

import java.util.Properties

import org.apache.kafka.clients.producer.KafkaProducer
import prestacop.DroneData
import prestacop.infraction.Infraction

/**
 * Wrapper around a Kafka producer following the Singleton design pattern
 */
trait KafkaProducerTrait {
  private val bootstrapServers = "163.172.191.74:9092"

  private val props = new Properties()
  props.put("bootstrap.servers", bootstrapServers)

  private var locationProducerInstance: KafkaProducer[Integer, DroneData] = _

  private var imageProducerInstance: KafkaProducer[String, Array[Byte]] = _

  private var infractionProducerInstance: KafkaProducer[String, Infraction] = _

  private var humanAssistanceProducerInstance: KafkaProducer[String, Int] = _

  /**
   * Get the a `KafkaProducer[Integer, DroneData]` following Singleton design pattern
   * @return Instance of `KafkaProducer[Integer, DroneData]`
   */
  def locationProducer: KafkaProducer[Integer, DroneData] = {
    locationProducerInstance match {
      case null => {
        props.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer")
        props.put("value.serializer", "prestacop.serialization.GenericSerializer")
        locationProducerInstance = new KafkaProducer[Integer, DroneData](props)
        locationProducerInstance
      }
      case _ => locationProducerInstance
    }
  }

  def imageProducer: KafkaProducer[String, Array[Byte]] = {
    imageProducerInstance match {
      case null => {
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
        props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer")
        imageProducerInstance = new KafkaProducer[String, Array[Byte]](props)
        imageProducerInstance
      }
      case _ => imageProducerInstance
    }
  }

  def infractionProducer: KafkaProducer[String, Infraction] = {
    infractionProducerInstance match {
      case null => {
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
        props.put("value.serializer", "prestacop.serialization.GenericSerializer")
        infractionProducerInstance = new KafkaProducer[String, Infraction](props)
        infractionProducerInstance
      }
      case _ => infractionProducerInstance
    }
  }
}
