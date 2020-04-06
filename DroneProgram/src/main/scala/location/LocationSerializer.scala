package location

import com.google.gson.Gson
import org.apache.kafka.common.serialization.Serializer

class LocationSerializer extends Serializer[Location] {
  private val gson = new Gson()

  override def serialize(topic: String, data: Location): Array[Byte] = {
    data match {
      case (null) => null
      case _ => gson.toJson(data).getBytes()
    }
  }
}
