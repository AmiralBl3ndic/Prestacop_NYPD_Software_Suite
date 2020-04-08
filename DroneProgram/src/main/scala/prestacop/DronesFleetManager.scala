package prestacop

import java.time.Duration
import java.util.{Collections, Properties}

import org.apache.kafka.clients.consumer.KafkaConsumer

object DronesFleetManager extends App {
  val bootstrapServers = "163.172.191.74:9092"

  val props = new Properties()
  props.put("bootstrap.servers", bootstrapServers)
  props.put("auto.offset.reset", "latest")
  props.put("key.deserializer", "org.apache.kafka.common.serialization.IntegerDeserializer")
  props.put("value.deserializer", "prestacop.serialization.GenericDeserializer")
  props.put("group.id", "fdp-project")

  val consumer = new KafkaConsumer[Integer, DroneData](props)
  consumer.subscribe(Collections.singletonList("NYPD_DRONE_LOCATION_UPDATE_REPLICA_1"))

  println("DronesFleetManager started and listening for incoming data")

  while (true) {
    val records = consumer.poll(Duration.ofMillis(150))
    records.forEach(droneDataRecord => {
      val droneId = droneDataRecord.key()
      val recordValue: DroneData = droneDataRecord.value()

      val timeStamp = s"${recordValue.time.toLocalDate.toString} ${recordValue.time.toLocalTime.toString}"
      val droneLatitude = recordValue.location.latitude
      val droneLongitude = recordValue.location.longitude
      val droneBattery = recordValue.battery

      println(f"[$timeStamp] [Drone #$droneId]: 📍 (Lat=$droneLatitude, Lon=$droneLongitude) ⚡️ $droneBattery%1.2f")
    })
  }
}
