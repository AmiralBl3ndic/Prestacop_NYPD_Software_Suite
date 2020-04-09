package prestacop.location

/**
 * Trait that simply adds a prestacop.location to the class it is bound to
 */
trait LocationTrait {
  val location = new Location()
}
