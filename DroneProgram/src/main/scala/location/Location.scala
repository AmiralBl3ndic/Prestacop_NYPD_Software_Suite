package location

import scala.util.Random

class Location {
  private val NYC_WEST_LONGITUDE = -74.257159
  private val NYC_EAST_LONGITUDE = -73.699215
  private val NYC_NORTH_LATITUDE = 40.915568
  private val NYC_SOUTH_LATITUDE = 40.495992

  var longitude: Double = _
  var latitude: Double = _

  /**
   * Function that changes the coordinates of the drone
   */
  def updateRandom(): Unit = {
    val rd = new Random()

    longitude = rd.between(NYC_WEST_LONGITUDE, NYC_EAST_LONGITUDE)
    latitude = rd.between(NYC_SOUTH_LATITUDE, NYC_NORTH_LATITUDE)
  }
}
