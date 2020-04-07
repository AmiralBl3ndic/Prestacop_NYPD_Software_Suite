package prestacop.infraction

import org.apache.kafka.clients.producer.ProducerRecord
import prestacop.DroneTrait
import prestacop.kafka.KafkaProducerTrait

trait InfractionImageProducerRecordTrait extends KafkaProducerTrait with DroneTrait {
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
    println(s"[$topicName] [Drone #$droneId] Sent image for infraction #${infractionImage.imageId}")
  }
}
