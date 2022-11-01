import akka.actor.{Actor, ActorIdentity, ActorRef,
  Identify, Terminated, ReceiveTimeout, Stash}

import scala.concurrent.duration.*
import scala.language.postfixOps

object ProxyModule{
  case class TerminateRemoteAkkaSustem()
  case class ShutdownWaitEnded()
  case class OperationFailedHadToDestroyRemoteAkkaSystem()
  case class OperationFailedShutdownRemoteAkkaSystem()
  case class ShutdownWaitTerminationEnded()
  case class SutdownFailedHadToDestroyRemoteAkkaSystem()
  case class ShutdownRemoteAkkaSystemSuccess()
}

class ProxyModule(path: String, winCmd: Option[String],
                  unixCmd: Option[String], replyTo: ActorRef) extends Actor with Stash {
  import ProxyModule._

  def sendIdentifyRequest(): Unit = {
    val selection = context.actorSelection(path)
    selection ! Identify(path)
  }

  override def receive = identify

  var remoteActorSystemProcess: scala.sys.process.Process = null

  override def preStart(): Unit =
    import java.io.File
    //Start module
    remoteActorSystemProcess = CmdOperations.runCmdNoWait(winCmd, unixCmd, new File("."))
    //also we could use scheduler
    context.setReceiveTimeout(3 seconds) // send messages receiveTimeOut
    sendIdentifyRequest()
    super.preStart()

  def identify: Receive = {
    case ActorIdentity(`path`, Some(actor)) =>
      context.setReceiveTimeout(Duration.Undefined) //Do not send messages receiveTimeOut
      println(s"proxy module actor ${self.path.toString} switched to active state")
      unstashAll()
      context.become(active(actor))
      context.watch(actor) //We can watch only actorRef not actorSelection
    case ActorIdentity(`path`, None) =>
      println(s"[Warning] proxy module actor ${self.path.toString} did not find remote actor with path " +
        path
      )
    case ReceiveTimeout =>
      sendIdentifyRequest()
    case msg: Any => stash()

  }

  def active(actor: ActorRef): Receive = {
    case Terminated(actorRef) =>
      println(s"actor  $actorRef terminated")
      println(s"proxy module ${self.path.toString} in error state")
      context.become(shutdownState(actor))
      context.unwatch(actor)
      self ! TerminateRemoteAkkaSustem()

    case shared.Shutdown() =>
      context.become(shutdownState(actor))
      context.unwatch(actor)
      self ! shared.Shutdown()

    case msg: Any => actor forward msg
  }

  def shutdownState(actor: ActorRef): Receive = {
    case TerminateRemoteAkkaSustem() =>
      println(s"proxy module ${self.path.toString} started terminating actor system of actor $path")
      actor ! shared.Shutdown()
      implicit val ec = context.system.dispatcher
      context.system.scheduler.scheduleOnce(20 seconds, self, ShutdownWaitTerminationEnded())

    case shared.Shutdown() =>
      println(s"proxy module ${self.path.toString} started to shutdown actor system of actor $path")
      actor ! shared.Shutdown()
      implicit val ec = context.system.dispatcher
      context.system.scheduler.scheduleOnce(20 seconds, self, ShutdownWaitEnded())

    case ShutdownWaitEnded() =>
      if remoteActorSystemProcess.isAlive() then
        println(s"Error: proxy module ${self.path.toString} could not shutdown actor system of actor $path" +
          "Have to terminate process"
        )
        remoteActorSystemProcess.destroy()
        replyTo ! SutdownFailedHadToDestroyRemoteAkkaSystem()
      else
        println(s"proxy module ${self.path.toString} successfully made shutdown of actor system of actor $path")
        replyTo ! ShutdownRemoteAkkaSystemSuccess()
      end if
    case ShutdownWaitTerminationEnded() =>
      if remoteActorSystemProcess.isAlive() then
        println(s"Error: proxy module ${self.path.toString} could not shutdown actor system of actor $path" +
          "Have to terminate process"
        )
        remoteActorSystemProcess.destroy()
        replyTo ! OperationFailedHadToDestroyRemoteAkkaSystem()
      else
        println(s"proxy module ${self.path.toString} successfully made shutdown of actor system of actor $path")
        replyTo ! OperationFailedShutdownRemoteAkkaSystem()
      end if
    case msg: Any =>
      println(s"Warning: proxy module ${self.path.toString} ignore msg ${msg.toString} in shutdown state")
  }
}
