package prestacop

import java.util.concurrent.{ScheduledThreadPoolExecutor, TimeUnit}

import prestacop.infraction.{HumanAssistanceProducerRecordTrait, Infraction, InfractionImageProducerRecordTrait, InfractionProducerRecordTrait}
import prestacop.location.DroneLocationProducerRecordTrait

import scala.util.Random

object DroneProgram extends App
  with DroneLocationProducerRecordTrait
  with InfractionImageProducerRecordTrait
  with InfractionProducerRecordTrait
  with HumanAssistanceProducerRecordTrait {

  val random = new Random()
  val timer = new ScheduledThreadPoolExecutor(1)
  val durationBetweenMessages = 30L  // Send two messages by minute

  // Make drone send updates to
  val scheduledLocationTask = timer.scheduleAtFixedRate(
    sendLocationTask,
    2,
    durationBetweenMessages,
    TimeUnit.SECONDS)

  // Simulate drone having a prestacop.location before takeoff
  location.updateRandom()

  while (battery > 10) {  // Simulate drone stopping after battery dropped under 10%
    Thread.sleep(5000)  // Wait 5 seconds
    location.updateRandom()  // Simulate drone motion
    battery -= .05  // Simulate battery loss

    random.between(0, 10) match {
      case 0 => {  // 10% chances to detect an infraction
        // Simulate potential need for human assistance
        random.between(0, 100) match {
          // Infraction that requires human assistance is detected
          case 0 => { // Need for human assistance
            val infraction = new Infraction()

            sendInfractionImage(infraction.image)
            sendHumanAssistanceRequest(infraction.imageId)
          }

          // Infraction that does not require human assistance is detected
          case _ => {
            val infraction = new Infraction()

            sendInfractionImage(infraction.image)
            sendInfraction(infraction)
          }
        }
      }

      // Do nothing, no infraction is detected
      case _ =>
    }
  }
}
