package prestacop.serialization

import java.io.{ByteArrayInputStream, ObjectInputStream}

import org.apache.kafka.common.serialization.Deserializer

class GenericDeserializer[T] extends Deserializer[T] {
  override def deserialize(topic: String, data: Array[Byte]): T = {
    val byteIn = new ByteArrayInputStream(data)
    val objIn = new ObjectInputStream(byteIn)
    val obj = objIn.readObject().asInstanceOf[T]
    byteIn.close()
    objIn.close()
    obj
  }
}
