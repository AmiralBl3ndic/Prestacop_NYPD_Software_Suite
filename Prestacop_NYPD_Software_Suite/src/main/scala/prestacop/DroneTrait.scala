package prestacop

trait DroneTrait {
  val droneId: Int = new scala.util.Random().between(0, 9999999)

  var battery: Double = 100.00
}
