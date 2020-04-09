package prestacop.infraction

import org.apache.kafka.clients.producer.ProducerRecord
import prestacop.kafka.KafkaProducerTrait

trait InfractionProducerRecordTrait extends KafkaProducerTrait {
  protected val topicName = "NYPD_DRONE_INFRACTION"

  protected def getProducerRecord(infraction: Infraction): ProducerRecord[String, Infraction] = {
    new ProducerRecord[String, Infraction](
      topicName,
      infraction.imageId,
      infraction
    )
  }

  def sendInfraction(infraction: Infraction): Unit = {
    infractionProducer.send(getProducerRecord(infraction))
    println(s"[$topicName] Sent infraction #${infraction.imageId} (code = ${infraction.code})")
  }
}
