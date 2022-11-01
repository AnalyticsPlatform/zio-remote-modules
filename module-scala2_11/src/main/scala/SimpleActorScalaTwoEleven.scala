import TestMsgs.{AkkaMsgTest1, AkkaMsgTestActorRef}
import akka.actor._

import scala.sys.exit

object SimpleActorScalaTwoEleven {
  def apply(): Props = Props(new SimpleActorScalaTwoEleven())
}

class SimpleActorScalaTwoEleven extends Actor {
  override def receive: Receive = {
    case shared.Shutdown() => println(s"received shutdown command. Starting shutdown actor system")
      shutdownAkkaSystem(context.system)

    case AkkaMsgTestActorRef(msg, msg2, msg3, replyTo: Option[ActorRef]) =>
      println(s"Received akka msg AkkaMsgTestActorRef, msg is $msg $msg2 $msg3" +
        " Checking actorref"
      )
      replyTo match {
        case Some(ref) => println(s"Got actorRef ${ref.path.toString}. Sending answer")
          ref ! AkkaMsgTest1("Scala module 2.11 received msg from you", "Sending Answer:", "Test msg scala 2.11")
        case None =>
      }
    case msg: Any => println(s"[${self.path.toString}] received msg: " + msg.toString)
  }

  def shutdownAkkaSystem(system: ActorSystem, isFailed: Boolean = false) = {
    implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
    system.terminate().map(
      _ => {
        println(s"Successfully terminated ${system.name} actor system")
        if (isFailed) exit(1)
      }
    )
  }
}
