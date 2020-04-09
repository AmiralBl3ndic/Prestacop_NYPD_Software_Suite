package prestacop

import java.time._

import prestacop.location.Location

class DroneData(val location: Location, val battery: Double) extends Serializable {
  val time: LocalDateTime = LocalDateTime.now()
}
