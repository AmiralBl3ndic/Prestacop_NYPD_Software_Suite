import location.{Location, LocationTrait}
import org.apache.kafka.clients.producer.ProducerRecord

trait DroneLocationProducerRecordTrait extends KafkaProducerTrait with LocationTrait {
  val droneId: Int = new scala.util.Random().between(0, 9999999);

  val topicName = "NYPD_DRONE_LOCATION_UPDATE"

  def producerRecord(): ProducerRecord[Integer, DroneData] = {
    new ProducerRecord[Integer, DroneData](
      topicName,
      droneId,
      new DroneData(location)
    )
  }

  val sendLocationTask: Runnable = () => {
    locationProducer.send(producerRecord())
    println(s"[#$droneId] (Lat=${location.latitude}, Lon=${location.longitude})") // To test
  }
}
