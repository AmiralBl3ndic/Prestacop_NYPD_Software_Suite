package prestacop

import java.util.{Collections, Properties, Scanner}
import java.time.{Duration, LocalDateTime}

import com.redis._
import serialization.Parse.Implicits._
import org.apache.kafka.clients.consumer.KafkaConsumer
import prestacop.infraction.{Infraction, InfractionProducerRecordTrait}
import prestacop.serialization.GenericSerializer
import prestacop.serialization.GenericDeserializer

object HumanAssistanceManager extends App with InfractionProducerRecordTrait {
  ////////////////////////////////////////////////////////////////
  //                   CONFIGURATION & SETUP                    //
  ////////////////////////////////////////////////////////////////

  private val kafkaHumanAssistanceTopic = "NYPD_DRONE_HUMAN_ASSISTANCE"
  private val kafkaInfractionImagesTopic = "NYPD_DRONE_INFRACTION_IMAGE"

  val redis = new RedisClient("163.172.191.74", 6379)

  val props = new Properties()
  props.put("bootstrap.servers", "163.172.191.74:9092")
  props.put("group.id", "fdp-project")
  props.put("auto.offset.reset", "latest")
  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")

  props.put("value.deserializer", "org.apache.kafka.common.serialization.IntegerDeserializer")
  val humanAssistanceConsumer = new KafkaConsumer[String, Integer](props)

  props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer")
  val infractionImagesConsumer = new KafkaConsumer[String, Array[Byte]](props)

  humanAssistanceConsumer.subscribe(Collections.singletonList(kafkaHumanAssistanceTopic))
  infractionImagesConsumer.subscribe(Collections.singletonList(kafkaInfractionImagesTopic))

  val Serializer = new GenericSerializer[CachedHumanAssistanceRequest]
  val Deserializer = new GenericDeserializer[CachedHumanAssistanceRequest]

  private val scanner = new Scanner(System.in)

  ////////////////////////////////////////////////////////////////
  //                        BUSINESS LOGIC                      //
  ////////////////////////////////////////////////////////////////

  /**
   * Background streams listener
   */
  val backgroundKafkaStreamsConsumer: Thread = {
    new Thread {
      override def run(): Unit = {
        try {
          while (true) {
            val newAssistanceRequests = humanAssistanceConsumer.poll(Duration.ofMillis(100))
            val newInfractionImages = infractionImagesConsumer.poll(Duration.ofMillis(100))

            newAssistanceRequests.forEach(assistanceRequest => {
              val imageId = assistanceRequest.key()
              val droneId = assistanceRequest.value()

              val cachedImage = redis.get[Array[Byte]](s"image:$imageId")
              cachedImage match {
                case Some(image) => {
                  redis.del(s"image:$imageId")

                  val record = CachedHumanAssistanceRequest(droneId, imageId, image)
                  val serializedRecord = Serializer.serialize("", record)

                  redis.rpush("human_assistance", serializedRecord)
                }
                case None => redis.set(s"pending_human_assistance:$imageId", droneId)
              }
            })

            newInfractionImages.forEach(infractionImage => {
              val imageId = infractionImage.key()
              val image = infractionImage.value()

              val cachedInfraction = redis.get[Int](s"pending_human_assistance:$imageId")
              cachedInfraction match {
                case Some(droneId) => {
                  redis.del(s"pending_human_assistance:$imageId")

                  val record = CachedHumanAssistanceRequest(droneId, imageId, image)
                  val serializedRecord = Serializer.serialize("", record)

                  redis.rpush("human_assistance", serializedRecord)
                }
                case None => redis.set(s"image:$imageId", image)  // This allows to share this cache with other services
              }
            })
          }
        } catch {
          case _: Exception =>
        }
      }
    }
  }

  backgroundKafkaStreamsConsumer.start()  // Listen for streams updates in background

  println("HumanAssistanceManager started and awaiting for drone requests")
  while (true) {
    try {
      val redisCachedRequests = redis.lrange[Array[Byte]]("human_assistance", 0, -1)

      redisCachedRequests match {
        case Some(serializedListOfRequests) => {
          redis.lpop[Array[Byte]]("human_assistance")  // Remove first assistance request from cache
          val validRequests = serializedListOfRequests
            .filter(_.isDefined)  // Should not change anything
            .map(_.get)
            .map((serializedRecord: Array[Byte]) => Deserializer.deserialize("", serializedRecord))

          val requestsCount = validRequests.length

          requestsCount match {
            case 0 => {
              println(s"[${LocalDateTime.now().toLocalTime}] 0 pending assistance request")
              Thread.sleep(15000)  // Pause for 15 seconds before attempting new request
            }
            case _ => {
              println(s"[${LocalDateTime.now().toLocalTime}] $requestsCount pending assistance request" + {
                requestsCount match {
                  case x if x == 1 => ""
                  case _ => "s"
                }
              })

              val request = validRequests.head

              val infraction = new Infraction()
              infraction.imageId = request.imageId
              do {
                println("================================================================================")
                println(s"Solving assistance request for infraction #${request.imageId}")
                println(s"(POC: image should be displayed to the operator in a GUI)")
                print("Enter infraction code: ")

                infraction.code = scanner.nextInt()
              } while (infraction.code < 0 || infraction.code > 99)

              sendInfraction(infraction)
            }
          }
        }
        case None => println("Unable to fetch human assistance requests")  // Should never be displayed
      }
    } catch {
      case _: Throwable => println("An error occurred")
    }

    Thread.sleep(1000) // Wait 1 second
  }
}
