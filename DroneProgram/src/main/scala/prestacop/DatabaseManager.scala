package prestacop

import org.mongodb.scala._
import org.mongodb.scala.bson.ObjectId
import prestacop.database.InfractionMongoRecord

import scala.util.Random

/**
 * This program manages the database records
 */
object DatabaseManager extends App {
  private val mongoConnectionString = "mongodb://prestacop:prestacop@163.172.191.74:27017"
  System.setProperty("org.mongodb.async.type", "netty")

  val mongoClient = MongoClient(mongoConnectionString)
  val db = mongoClient
    .getDatabase("prestacop")
    .withCodecRegistry(InfractionMongoRecord.codecRegistry)

  val dronesInfractionRecords = db.getCollection[InfractionMongoRecord]("drones_infraction_records")

  val rd = new Random()

  val testRecord = InfractionMongoRecord(new ObjectId(), 123456789, rd.nextBytes(2048), 42)


  InfractionMongoRecord.insertOne(dronesInfractionRecords, testRecord)

  sys.exit(0)
}
