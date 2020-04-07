package prestacop

import java.util.concurrent.{ScheduledThreadPoolExecutor, TimeUnit}

import prestacop.infraction.{Infraction, InfractionImageProducerRecordTrait, InfractionProducerRecordTrait}
import prestacop.location.DroneLocationProducerRecordTrait

import scala.util.Random

object DroneProgram extends App
  with DroneLocationProducerRecordTrait
  with InfractionImageProducerRecordTrait
  with InfractionProducerRecordTrait {

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

  while (true) {
    Thread.sleep(100)  // Wait 5 seconds
    location.updateRandom()  // Simulate drone motion

    random.between(.0, 1.0) match {
      case i if i >= .9 => {
        // Simulate potential need for human assistance
        random.between(0, 100) match {
          // Infraction that requires human assistance is detected
          case 0 => { // Need for human assistance
            val infraction = new Infraction()

            sendInfractionImage(infraction.image)
          }

          // Infraction that does not require human assistance is detected
          case _ => {
            val infraction = new Infraction()

            sendInfraction(infraction)
          }
        }
      }

      // Do nothing, no infraction is detected
      case _ =>
    }
  }
}
