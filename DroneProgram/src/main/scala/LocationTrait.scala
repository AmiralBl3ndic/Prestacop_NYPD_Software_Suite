import scala.util.Random

trait LocationTrait {
  val NYC_WEST_LONGITUDE = -74.257159
  val NYC_EAST_LONGITUDE = -73.699215
  val NYC_NORTH_LATITUDE = 40.915568
  val NYC_SOUTH_LATITUDE = 40.495992

  var latitude: Double = _
  var longitude: Double = _

  private val _rd = new Random()

  /**
   * Function that changes the coordinates of the drone
   */
  def updateLocation(): Unit = {
    longitude = _rd.between(NYC_WEST_LONGITUDE, NYC_EAST_LONGITUDE)
    latitude = _rd.between(NYC_SOUTH_LATITUDE, NYC_NORTH_LATITUDE)
  }
}
