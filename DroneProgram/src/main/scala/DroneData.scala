import java.time._
import location.Location

class DroneData(location: Location) extends Serializable {
  private val time: LocalDateTime = LocalDateTime.now()
}
