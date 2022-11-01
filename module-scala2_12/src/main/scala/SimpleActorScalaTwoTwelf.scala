//import akka.actor._
//
//import scala.sys.exit
//
//object SimpleActorScalaTwoTwelf{
//  def apply(): Props = Props(new SimpleActorScalaTwoTwelf())
//}
//
//class SimpleActorScalaTwoTwelf extends Actor {
//  override def receive: Receive = {
//    case shared.Shutdown() => println(s"received shutdown command. Starting shutdown actor system")
//      shutdownAkkaSystem(context.system)
//    case msg: Any => println(s"[${self.path.toString}] received msg: " + msg.toString)
//  }
//
//  def shutdownAkkaSystem(system: ActorSystem, isFailed: Boolean = false) = {
//    implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
//    system.terminate().map(
//      _ => {
//        println(s"Successfully terminated ${system.name} actor system")
//        if (isFailed) exit(1)
//      }
//    )
//  }
//}
