package prestacop.location

import org.apache.kafka.clients.producer.ProducerRecord
import prestacop.DroneData
import prestacop.kafka.KafkaProducerTrait

trait DroneLocationProducerRecordTrait extends KafkaProducerTrait with LocationTrait {
  private val droneId: Int = new scala.util.Random().between(0, 9999999);

  private val topicName = "NYPD_DRONE_LOCATION_UPDATE"

  def producerRecord: ProducerRecord[Integer, DroneData] = {
    new ProducerRecord[Integer, DroneData](
      topicName,
      droneId,
      new DroneData(location)
    )
  }

  val sendLocationTask: Runnable = () => {
    locationProducer.send(producerRecord)
    println(s"[NYPD_DRONE_LOCATION_UPDATE] Sent (Lat=${location.latitude}, Lon=${location.longitude}) with drone #$droneId") // To test
  }
}
