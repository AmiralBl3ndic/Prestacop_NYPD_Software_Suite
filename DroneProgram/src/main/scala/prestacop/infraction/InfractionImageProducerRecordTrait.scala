package prestacop.infraction

import org.apache.kafka.clients.producer.ProducerRecord
import prestacop.kafka.KafkaProducerTrait

trait InfractionImageProducerRecordTrait extends KafkaProducerTrait {
  private val topicName = "NYPD_DRONE_INFRACTION_IMAGE"

  private def getProducerRecord(infractionImage: InfractionImage): ProducerRecord[String, Array[Byte]] = {
    new ProducerRecord[String, Array[Byte]](
      topicName,
      infractionImage.imageId,
      infractionImage.image
    )
  }

  def sendInfractionImage(infractionImage: InfractionImage): Unit = {
    imageProducer.send(getProducerRecord(infractionImage))
    println(s"[NYPD_DRONE_INFRACTION_IMAGE] Sent image #${infractionImage.imageId}")
  }
}
