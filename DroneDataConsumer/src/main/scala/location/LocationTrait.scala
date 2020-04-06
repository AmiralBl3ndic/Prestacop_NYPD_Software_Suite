package location

/**
 * Trait that simply adds a location to the class it is bound to
 */
trait LocationTrait {
  val location = new Location()
}
