import TestMsgs.{AkkaMsgTest1, AkkaMsgTestActorRef}
import akka.actor.{Actor, ActorSelection, ActorSystem, Props, ActorRef}

import scala.sys.exit

object RunMainActor:
  def props(): Props = Props(new RunMainActor())
  case class StartWork()
  case class StartTest1()
  case class StartTest2()
  case class WorkDone()

class RunMainActor() extends Actor:
  import RunMainActor._
  override def preStart(): Unit =
//    self ! StartWork()
    super.preStart()

  var simple2_11: ActorRef = null
  var simple2_12: ActorRef = null
  var simple3: ActorRef = null

  def initProxyModuleActor(actorRemoteName: String, actorSystemName: String, winScript: String,
                           unixScript: String, proxyActorName: String): ActorRef = {
    var port = PortOperations.isPortAvailable(0)
    val pathScala2_11 = s"akka.tcp://$actorSystemName@0.0.0.0:$port/user/$actorRemoteName"
    context.actorOf(Props(new ProxyModule(pathScala2_11,
      Some(s"$winScript --port $port"), Some(s"$unixScript --port $port"), self
    )), proxyActorName)
  }

  override def receive: Receive = {
    case StartWork() =>
      simple2_11 = initProxyModuleActor("simple-scala-2-11", "backendScalaTwoEleven",
        "module-scala2_11.bat", "module-scala2_11", "simple2_11Proxy"
      )
      //actor remote path is s"akka.tcp://backendScalaTwotwelf@0.0.0.0:$port/user/simple-scala-2-12"
      simple2_12 = initProxyModuleActor("simple-scala-2-12", "backendScalaTwotwelf",
        "module-scala2_12.bat", "module-scala2_12", "simple2_12Proxy"
      )
      simple3 = initProxyModuleActor("simpleScalaThree", "backendScalaThree",
        "module-scala3.bat", "module-scala3", "simple3Proxy"
      )

      self ! StartTest1()
    case StartTest1() =>
      println(s"Started Test1: ")
      simple2_11 ! "Hello scala 2.11"
      simple2_12 ! "Hello scala 2.12"
      simple3 ! "Hello scala 3"
      simple2_11 ! AkkaMsgTest1("hello", "scala", "2.11")
      simple2_12 ! AkkaMsgTest1("hello", "scala", "2.12")
      simple3 ! AkkaMsgTest1("hello", "scala", "3")
      self ! StartTest2()
    case  StartTest2() =>
      println(s"Started Test2 Where we wait for response: ")
      simple2_11 ! AkkaMsgTestActorRef("test", "actorref", "on scala 2.11", Some(self))
    case AkkaMsgTest1(msg, msg1, msg3) =>
      println(s"Received msg from scala 2.11 module: Response:  $msg $msg1 $msg3")
      println("Sending shutdown commands to modules and shutdown yourself")
      simple2_11 ! shared.Shutdown()
      simple2_12 ! shared.Shutdown()
      simple3 ! shared.Shutdown()
      self ! WorkDone()
    case WorkDone() =>
      shutdownAkkaSystem(context.system)
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
