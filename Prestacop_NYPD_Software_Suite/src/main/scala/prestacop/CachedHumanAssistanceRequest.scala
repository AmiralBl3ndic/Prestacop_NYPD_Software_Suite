package prestacop

case class CachedHumanAssistanceRequest(droneId: Int, imageId: String, image: Array[Byte]) extends Serializable
