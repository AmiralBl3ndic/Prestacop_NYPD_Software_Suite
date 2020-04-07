package prestacop.location

import java.io.{ByteArrayInputStream, ObjectInputStream}

import org.apache.kafka.common.serialization.Deserializer

class LocationDeserializer extends Deserializer[Location] {
  override def deserialize(topic: String, data: Array[Byte]): Location = {
    val byteIn = new ByteArrayInputStream(data)
    val objIn = new ObjectInputStream(byteIn)
    val obj = objIn.readObject().asInstanceOf[Location]
    byteIn.close()
    objIn.close()
    obj
  }
}
