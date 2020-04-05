import org.apache.kafka.clients.producer.ProducerRecord

trait DroneLocationProducerRecordTrait extends KafkaProducerTrait with LocationTrait {
  val droneId: Int = 421337

  val topicName = "NYPD_DRONE_UPDATE"

  val producerRecord: ProducerRecord[String, String] = {
    new ProducerRecord[String, String](
      topicName,
      s"[#$droneId] (Lat=$latitude, Lon=$longitude)"
    )
  }

  val sendLocationTask = new Runnable {
    def run(): Unit = {
      producer.send(producerRecord)
      println(s"[#$droneId] (Lat=$latitude, Lon=$longitude)")  // To test
    }
  }
}
