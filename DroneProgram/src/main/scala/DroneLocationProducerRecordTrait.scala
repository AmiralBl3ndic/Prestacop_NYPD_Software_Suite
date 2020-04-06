import org.apache.kafka.clients.producer.ProducerRecord

trait DroneLocationProducerRecordTrait extends KafkaProducerTrait with LocationTrait {
  val droneId: Int = new scala.util.Random().between(0, 9999999);

  val topicName = "NYPD_DRONE_UPDATE"

  def producerRecord(): ProducerRecord[String, String] = {
    new ProducerRecord[String, String](
      topicName,
      s"[#$droneId] (Lat=${location.latitude}, Lon=${location.longitude})"
    )
  }

  val sendLocationTask: Runnable = new Runnable {
    def run(): Unit = {
      producer.send(producerRecord())
      println(s"[#$droneId] (Lat=${location.latitude}, Lon=${location.longitude})")  // To test
    }
  }
}
