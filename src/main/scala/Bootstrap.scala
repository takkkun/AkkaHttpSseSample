import akka.actor.ActorSystem
import akka.NotUsed
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.sse.ServerSentEvent

import scala.concurrent.duration._
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_DATE_TIME

import scala.io.StdIn

object Bootstrap {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()

    val binding = Http().bindAndHandle(route, "localhost", 8080)

    StdIn.readLine()

    implicit val executionContext = system.dispatcher

    binding
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }

  private def route: Route = {
    import akka.http.scaladsl.server.Directives._
    import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling._

    path("") {
      get {
        complete {
          Source
            .tick(2.seconds, 2.seconds, NotUsed)
            .map(_ => LocalDateTime.now)
            .map(time => ServerSentEvent(data = ISO_DATE_TIME.format(time)))
        }
      }
    }
  }
}
