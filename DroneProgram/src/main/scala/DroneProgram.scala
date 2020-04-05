import java.util.concurrent._


object DroneProgram extends App with KafkaProducerTrait with DroneLocationProducerRecordTrait {
  val timer = new ScheduledThreadPoolExecutor(1)
  val durationBetweenMessages = 60L  // Send a message by minute

  // Make drone send updates to
  val scheduledLocationTask = timer.scheduleAtFixedRate(
    sendLocationTask,
    5,
    durationBetweenMessages,
    TimeUnit.SECONDS)


  while (true) {
    Thread.sleep(1000)  // Wait 1 second
    updateLocation()  // Simulate drone motion
  }
}
