package prestacop.infraction

import org.apache.kafka.clients.producer.ProducerRecord
import prestacop.DroneTrait
import prestacop.kafka.KafkaProducerTrait

trait InfractionProducerRecordTrait extends KafkaProducerTrait with DroneTrait {
  private val topicName = "NYPD_DRONE_INFRACTION"

  private def getProducerRecord(infraction: Infraction): ProducerRecord[String, Infraction] = {
    new ProducerRecord[String, Infraction](
      topicName,
      infraction.imageId,
      infraction
    )
  }

  def sendInfraction(infraction: Infraction): Unit = {
    infractionProducer.send(getProducerRecord(infraction))
    println(s"[$topicName] [Drone #$droneId] Sent infraction #${infraction.imageId} (code = ${infraction.code})")
  }
}
