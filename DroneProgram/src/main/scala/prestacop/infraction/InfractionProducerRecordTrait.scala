package prestacop.infraction

import org.apache.kafka.clients.producer.ProducerRecord
import prestacop.kafka.KafkaProducerTrait

trait InfractionProducerRecordTrait extends KafkaProducerTrait {
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
    println(s"[NYPD_DRONE_INFRACTION] Sent infraction #${infraction.imageId} (code = ${infraction.code})")
  }
}
