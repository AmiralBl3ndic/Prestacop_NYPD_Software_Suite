package prestacop.infraction

import org.apache.kafka.clients.producer.ProducerRecord
import prestacop.{DroneData, DroneTrait}
import prestacop.kafka.KafkaProducerTrait

trait HumanAssistanceProducerRecordTrait extends KafkaProducerTrait with DroneTrait {
  private val topicName = "NYPD_DRONE_HUMAN_ASSISTANCE"

  private def getProducerRecord(imageId: String, droneId: Int): ProducerRecord[String, Integer] = {
    new ProducerRecord[String, Integer](
      topicName,
      imageId,
      droneId
    )
  }

  def sendHumanAssistanceRequest(imageId: String): Unit = {
    humanAssistanceProducer.send(getProducerRecord(imageId, droneId))

    println(s"[$topicName] [Drone #$droneId] Sent human assistance request for infraction #$imageId")
  }
}
