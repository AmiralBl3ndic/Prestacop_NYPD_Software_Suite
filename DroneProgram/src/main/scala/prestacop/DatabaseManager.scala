package prestacop

import java.time.Duration
import java.util.{Collections, Properties}

import com.redis._
import serialization.Parse.Implicits._
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.mongodb.scala._
import org.mongodb.scala.bson.ObjectId
import prestacop.database.InfractionMongoRecord
import prestacop.infraction.Infraction

import scala.collection.mutable.ListBuffer


/**
 * This program manages the database records
 */
object DatabaseManager extends App {
  private val bootstrapServers = "163.172.191.74:9092"
  private val kafkaInfractionsTopic = "NYPD_DRONE_INFRACTION"
  private val kafkaInfractionImagesTopic = "NYPD_DRONE_INFRACTION_IMAGE"
  private val mongoConnectionString = "mongodb://prestacop:prestacop@163.172.191.74:27017"

  System.setProperty("org.mongodb.async.type", "netty")

  val props = new Properties()
  props.put("bootstrap.servers", bootstrapServers)
  props.put("group.id", "fdp-project")
  props.put("auto.offset.reset", "latest")
  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer", "prestacop.serialization.GenericDeserializer")

  val redis = new RedisClient("163.172.191.74", 6379)

  val mongoClient = MongoClient(mongoConnectionString)
  val db = mongoClient
    .getDatabase("prestacop")
    .withCodecRegistry(InfractionMongoRecord.codecRegistry)
  val dronesInfractionRecordsCollection = db.getCollection[InfractionMongoRecord]("drones_infraction_records")

  val kafkaInfractionsConsumer = new KafkaConsumer[String, Infraction](props)
  kafkaInfractionsConsumer.subscribe(Collections.singletonList(kafkaInfractionsTopic))

  props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer")
  val kafkaInfractionImagesConsumer = new KafkaConsumer[String, Array[Byte]](props)
  kafkaInfractionImagesConsumer.subscribe(Collections.singletonList(kafkaInfractionImagesTopic))

  println("DatabaseManager started and listening for incoming data")

  while (true) {
    val newInfractions = kafkaInfractionsConsumer.poll(Duration.ofMillis(100))
    val newInfractionImages = kafkaInfractionImagesConsumer.poll(Duration.ofMillis(100))

    newInfractions.forEach(infraction => {

      val infractionId = infraction.key()
      val infractionCode = infraction.value().code

      // Attempt to retrieve cached image with same infraction id
      val redisImage = redis.get[Array[Byte]](s"image:$infractionId")
      redisImage match {
        case Some(image) => {  // Cached image found
          redis.del(s"image:$infractionId")  // Delete cached image

          insertDroneInfractionMongoRecord(infractionId, image, infractionCode)
        }
        // No cached image found, caching infraction code
        case None => redis.set(s"infraction:$infractionId", infractionCode)
      }
    })

    newInfractionImages.forEach(infractionImageRecord => {
      val infractionId = infractionImageRecord.key()
      val image = infractionImageRecord.value()

      // Attempt to retrieve cached infraction with same infraction id
      val redisInfraction = redis.get[Int](s"infraction:$infractionId")
      redisInfraction match {
        case Some(infractionCode) => {  // Cached infraction found
          redis.del(s"infraction:$infractionId")  // Delete cached infraction

          insertDroneInfractionMongoRecord(infractionId, image, infractionCode)
        }
        // No cached infraction found, caching image
        case None => redis.set(s"image:$infractionId", image)
      }
    })
  }

  /**
   * Insert a record in the drones_infraction_records collection
   * @param infractionId Identifier of the infraction
   * @param image Image of the infraction
   * @param code Code of the infraction
   */
  def insertDroneInfractionMongoRecord(infractionId: String, image: Array[Byte], code: Int): Unit = {
    println(s"Inserting infraction #$infractionId into database")
    val record = new InfractionMongoRecord(new ObjectId(), infractionId, image, code)
    InfractionMongoRecord.insertOne(dronesInfractionRecordsCollection, record)
  }
}
