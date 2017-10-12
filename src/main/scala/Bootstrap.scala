import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.stream.actor.ActorPublisher
import akka.stream.scaladsl.Source
import redis.RedisClient

import scala.io.StdIn

object Bootstrap {
  private final val REDIS_HOST = "localhost"

  private final val REDIS_PORT = 6379

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()

    val binding = Http().bindAndHandle(buildRoute(), "localhost", 8080)

    StdIn.readLine()

    implicit val executionContext = system.dispatcher

    binding
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }

  private final val SSE_RETRY: Option[Int] = Some(5000)

  private def buildRoute()(implicit system: ActorSystem): Route = {
    import akka.http.scaladsl.server.Directives._
    import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling._

    Seq(
      path("publish") {
        post {
          formFields(('channel, 'message)).tapply {
            case (channel, message) =>
              complete {
                publish(channel, message)
                s"""published "$message" to $channel"""
              }
          }
        }
      },
      path("subscribe") {
        get {
          parameters('channel).tapply {
            case Tuple1(channel) =>
              complete {
                subscribe(channel).map { message =>
                  ServerSentEvent(
                    data  = message,
                    retry = SSE_RETRY
                  )
                }
              }
          }
        }
      }
    ).reduce(_ ~ _)
  }

  private def publish(channel: String, message: String)(implicit system: ActorSystem): Unit = {
    val redis = RedisClient(REDIS_HOST, REDIS_PORT)
    redis.publish(channel, message)
  }

  private def subscribe(channel: String)(implicit system: ActorSystem): Source[String, NotUsed] = {
    val subscribeActorRef = system.actorOf(SubscribeActor.props(REDIS_HOST, REDIS_PORT, channel))
    val publisher = ActorPublisher[String](subscribeActorRef)
    Source.fromPublisher(publisher)
  }
}
