package location

import java.io.{ByteArrayOutputStream, ObjectOutputStream}

import org.apache.kafka.common.serialization.Serializer

class LocationSerializer extends Serializer[Location] {
  override def serialize(topic: String, data: Location): Array[Byte] = {
    val byteOut = new ByteArrayOutputStream()
    val objOut = new ObjectOutputStream(byteOut)
    objOut.writeObject(data)
    objOut.close()
    byteOut.close()
    byteOut.toByteArray
  }
}
