package serialization

import java.io.{ByteArrayOutputStream, ObjectOutputStream}

import org.apache.kafka.common.serialization.Serializer

class GenericSerializer[T] extends Serializer[T] {
  override def serialize(topic: String, data: T): Array[Byte] = {
    data match {
      case null => null
      case _ => {
        val byteOut = new ByteArrayOutputStream()
        val objOut = new ObjectOutputStream(byteOut)
        objOut.writeObject(data)
        objOut.close()
        byteOut.close()
        byteOut.toByteArray
      }
    }
  }
}
