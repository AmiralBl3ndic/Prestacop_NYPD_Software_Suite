package prestacop

import java.time.Duration
import java.util.{Collections, Properties}

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

  var pendingInfractions: ListBuffer[Infraction] = new ListBuffer[Infraction]
  var pendingImages: ListBuffer[(String, Array[Byte])] = new ListBuffer[(String, Array[Byte])]

  println("DatabaseManager started and listening for incoming data")

  while (true) {
    val newInfractions = kafkaInfractionsConsumer.poll(Duration.ofMillis(100))
    val newInfractionImages = kafkaInfractionImagesConsumer.poll(Duration.ofMillis(100))

    newInfractions.forEach(infraction => {

      val infractionId = infraction.key()
      val infractionCode = infraction.value().code

      val imageIndex = pendingImages.map(_._1).indexOf(infractionId)
      imageIndex match {  // Check if an image exists with given id
        case -1 => {
          pendingInfractions += {
            val inf = new Infraction()
            inf.code = infractionCode
            inf.imageId = infractionId
            inf
          }
          println(s"Received infraction #$infractionId but no matching image found yet")
        }
        case _ => {
          val imageTuple = pendingImages(imageIndex)
          pendingImages -= imageTuple

          println(s"Inserting infraction #$infractionId into database")
          val image = imageTuple._2
          val record = new InfractionMongoRecord(new ObjectId(), infractionId, image, infractionCode)
          InfractionMongoRecord.insertOne(dronesInfractionRecordsCollection, record)
        }
      }
    })

    newInfractionImages.forEach(infractionImageRecord => {
      val infractionId = infractionImageRecord.key()
      val infractionImage = infractionImageRecord.value()

      val infractionIndex = pendingInfractions.map(_.imageId).indexOf(infractionId)
      infractionIndex match {
        case -1 => {
          pendingImages += { (infractionId, infractionImage) }
          println(s"Received image #$infractionId but no matching infraction found yet")
        }
        case _ => {
          val infraction = pendingInfractions(infractionIndex)
          pendingInfractions -= infraction

          println(s"Inserting infraction #$infractionId into database")
          val record = new InfractionMongoRecord(new ObjectId(), infractionId, infractionImage, infraction.code)
          InfractionMongoRecord.insertOne(dronesInfractionRecordsCollection, record)
        }
      }
    })
  }
}
