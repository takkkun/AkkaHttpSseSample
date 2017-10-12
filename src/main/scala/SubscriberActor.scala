import java.net.InetSocketAddress
import java.nio.charset.Charset

import akka.actor.Props
import akka.stream.actor.ActorPublisher
import redis.actors.RedisSubscriberActor
import redis.api.pubsub.{ PMessage, Message }

class SubscriberActor(host: String, port: Int, channel: String)
  extends RedisSubscriberActor(new InetSocketAddress(host, port), Seq(channel), Seq.empty, None, _ => ())
  with ActorPublisher[String] {

  private final val UTF8 = Charset.forName("UTF-8")

  def onMessage(m: Message): Unit =
    onNext(m.data.decodeString(UTF8))

  def onPMessage(pm: PMessage): Unit =
    onNext(pm.data.decodeString(UTF8))
}

object SubscriberActor {
  def props(host: String, port: Int, channel: String): Props =
    Props(classOf[SubscriberActor], host, port, channel)
}
