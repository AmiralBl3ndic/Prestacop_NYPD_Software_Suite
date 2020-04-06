import java.util.concurrent._

object DroneProgram extends App with KafkaProducerTrait with DroneLocationProducerRecordTrait {
  val timer = new ScheduledThreadPoolExecutor(1)
  val durationBetweenMessages = 30L  // Send two messages by minute

  // Make drone send updates to
  val scheduledLocationTask = timer.scheduleAtFixedRate(
    sendLocationTask,
    2,
    durationBetweenMessages,
    TimeUnit.SECONDS)

  // Simulate drone having a location before takeoff
  location.updateRandom()

  while (true) {
    Thread.sleep(5000)  // Wait 5 seconds
    location.updateRandom()  // Simulate drone motion
  }
}
