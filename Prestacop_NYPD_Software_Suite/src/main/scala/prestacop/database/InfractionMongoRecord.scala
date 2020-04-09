package prestacop.database

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import org.mongodb.scala._
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry

case class InfractionMongoRecord(_id: ObjectId, infractionId: String, image: Array[Byte], code: Integer)

object InfractionMongoRecord {
  def apply(infractionId: String, code: Integer, image: Array[Byte]): InfractionMongoRecord = new InfractionMongoRecord(new ObjectId(), infractionId, image, code)

  def codecRegistry: CodecRegistry = {
    fromRegistries(
      fromProviders(classOf[InfractionMongoRecord]),
      DEFAULT_CODEC_REGISTRY
    )
  }

  /**
   * Insert a record in a collection
   * @param collection Collection to insert in
   * @param record Record to insert in the database
   */
  def insertOne(collection: MongoCollection[InfractionMongoRecord], record: InfractionMongoRecord): Unit = {
    Await.result(collection.insertOne(record).toFuture, Duration.Inf)
  }
}
