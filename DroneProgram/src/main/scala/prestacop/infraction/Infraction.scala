package prestacop.infraction

import scala.util.Random

class Infraction extends Serializable {
  var code: Int = new Random().between(0, 100)
  var image: Array[Byte] = new Random().nextBytes(2048)

  /**
   * Generates a random bytes array and sets it as the value of the "image" property
   */
  def generateRandomImage(): Unit = {
    image = new Random().nextBytes(2048)
  }

  /**
   * Generates a random infraction code between 0 and 99 (inclusive) and sets it as the value of the "code" property
   */
  def generateRandomCode(): Unit = {
    code = new Random().between(0, 100)
  }

  /**
   * Generates an id based on the image bytes
   * @return Identifier generated upon image bytes
   */
  def imageId: String = {
    val rd = new Random()
    Math.abs(image.map(_.toDouble).reduce((a, b) => a + rd.between(42, 1337) * b)).toString.replace(".", "")
  }
}
