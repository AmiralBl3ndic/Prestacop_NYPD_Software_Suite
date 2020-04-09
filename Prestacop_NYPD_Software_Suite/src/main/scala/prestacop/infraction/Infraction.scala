package prestacop.infraction

import scala.util.Random

class Infraction extends Serializable {
  var code: Int = new Random().between(0, 100)

  var imageId: String = _

  // Generate a random imageId
  image

  /**
   * Generates a random bytes array and sets it as the value of the "image" property
   * @return Image as an array of bytes
   */
  def image: InfractionImage = {
    val imageBytes = new Random().nextBytes(4096)
    imageId = generateImageIdFromBytes(imageBytes)
    InfractionImage(imageId, imageBytes)
  }

  /**
   * Generate a random id from an array of bytes (random hashing)
   * @param bytes Bytes to use for hashing
   * @return String consisting of numbers only
   */
  private def generateImageIdFromBytes(bytes: Array[Byte]): String = {
    val rd = new Random()
    Math.abs(
        bytes.map(_.toDouble)
        .reduce((a, b) => a + rd.between(42, 1337) * b)
      )
      .toString
      .replace(".", "")
  }

  /**
   * Generates a random infraction code between 0 and 99 (inclusive) and sets it as the value of the "code" property
   */
  def generateRandomCode(): Unit = {
    code = new Random().between(0, 100)
  }
}
