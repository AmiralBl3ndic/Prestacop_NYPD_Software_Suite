package prestacop.infraction

import prestacop.DroneTrait

trait DroneInfractionProducerRecordTrait extends InfractionProducerRecordTrait with DroneTrait {
  override def sendInfraction(infraction: Infraction): Unit = {
    infractionProducer.send(getProducerRecord(infraction))
    println(s"[$topicName] [Drone #$droneId] Sent infraction #${infraction.imageId} (code = ${infraction.code})")
  }
}
