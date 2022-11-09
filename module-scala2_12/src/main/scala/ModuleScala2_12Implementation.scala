package app.zio.server

import app.zio.grpc.remote.clientMsgs.ZioClientMsgs.ZZioGrpcRemote
import app.zio.grpc.remote.clientMsgs.{ShutDown, ZioMsgTestReply}
import io.grpc.Status
import zio.Console.printLine
import zio.ZIO

object ModuleScala2_12ServerImplementation extends ZZioGrpcRemote[Any, Any] {
  def sendShutDown(request: ShutDown): ZIO[Any, Status, ZioMsgTestReply] = {
    ZIO.done(zio.Exit.Success(ZioMsgTestReply("Started shutdown")))
  }

  def sendZioMsgTest1(request: app.zio.grpc.remote.clientMsgs.ZioMsgTest1)
  : ZIO[Any, Status, ZioMsgTestReply] = {
    printLine(s"Got request: $request").orDie zipRight //Can we not die here?
      ZIO.succeed(ZioMsgTestReply(s"Module scala 3: Successfully received ZioMsgTest1"))
  }

  def sendZioMsgTest2Array(request: app.zio.grpc.remote.clientMsgs.ZioMsgTest2Array)
  : ZIO[Any, Status, ZioMsgTestReply] = {
    printLine(s"Got request: $request").orDie zipRight //Can we not die here?
      ZIO.succeed(ZioMsgTestReply(s"Module scala 3: Successfully received ZioMsgTest2Array"))
  }

  def sendZioMsgTest3Map(request: app.zio.grpc.remote.clientMsgs.ZioMsgTest3Map):
  ZIO[Any, Status, ZioMsgTestReply] = {
    printLine(s"Got request: $request").orDie zipRight //Can we not die here?
      ZIO.succeed(ZioMsgTestReply(s"Module scala 3: Successfully received ZioMsgTest3Map"))
  }
}
