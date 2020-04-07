package prestacop.location

import org.apache.kafka.clients.producer.ProducerRecord
import prestacop.{DroneData, DroneTrait}
import prestacop.kafka.KafkaProducerTrait

trait DroneLocationProducerRecordTrait extends KafkaProducerTrait with DroneTrait with LocationTrait {
  private val topicName = "NYPD_DRONE_LOCATION_UPDATE"

  private def getProducerRecord: ProducerRecord[Integer, DroneData] = {
    new ProducerRecord[Integer, DroneData](
      topicName,
      droneId,
      new DroneData(location)
    )
  }

  val sendLocationTask: Runnable = () => {
    locationProducer.send(getProducerRecord)
    println(s"[$topicName] [Drone #$droneId] Sent (Lat=${location.latitude}, Lon=${location.longitude})")
  }
}
