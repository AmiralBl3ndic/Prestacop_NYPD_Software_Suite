import java.time._
import location.Location

class DroneData(val location: Location) extends Serializable {
  val time: LocalDateTime = LocalDateTime.now()
}
