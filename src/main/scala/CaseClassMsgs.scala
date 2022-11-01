package TestMsgs

import akka.actor.ActorRef

//case class AkkaMsgTest1(msg: String, ex: Exception, a: Set[Int])
case class AkkaMsgTest1(msg: String, msg2: String, msg3: String)
case class AkkaMsgTestActorRef(msg: String, msg2: String, msg3: String, replyTo: Option[ActorRef])
