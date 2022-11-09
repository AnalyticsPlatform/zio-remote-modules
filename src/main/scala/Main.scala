import com.typesafe.config.*
import org.rogach.scallop.ScallopConf

import scala.sys.exit
import io.grpc.ManagedChannelBuilder
import zio.Console.*
import scalapb.zio_grpc.ZManagedChannel
import zio.*

import app.zio.grpc.remote.clientMsgs.*

object ClientApp extends ZIOAppDefault {
  def parseArgs(args: List[String]): ZIO[Any, Throwable, (String, String)] = {
    try {
      import org.rogach.scallop.ScallopConfBase
      val appArgs = AppArgs(args)
      val host: String = appArgs.host.toOption.get
      val port: String = appArgs.port.toOption.get.toInt.toString
      ZIO.succeed((host, port))
    } catch {
      case e: Throwable => ZIO.fail(e)
    }
  }

  def clientSendMsgs(prefix: String = "3") =
    for {
      f <- ZioClientMsgs.ZioGrpcRemoteClient.sendZioMsgTest1(ZioMsgTest1("hello", "scala", prefix))
      _ <- printLine(f.msg)
      r <- ZioClientMsgs.ZioGrpcRemoteClient.sendZioMsgTest2Array(ZioMsgTest2Array(Seq("hello", "scala", prefix)))
      _ <- printLine(r.msg)
      r <- ZioClientMsgs.ZioGrpcRemoteClient.sendZioMsgTest3Map(ZioMsgTest3Map(
        Map("msg1" -> "hello", "msg2" -> "scala", "msg3" -> prefix)
      ))
      _ <- printLine(r.msg)
      _ <- printLine(s"Sending shutdown to scala $prefix module")
      _ <- ZioClientMsgs.ZioGrpcRemoteClient.sendShutDown(ShutDown())
      _ <- printLine(r.msg)
    } yield ()

  override def run =
    for {
      args <- getArgs
      clientLayer <- parseArgs(args.toList).map(
        a => {
          println("host of server is " + a._1 + " and port is " + a._2)
          ZioClientMsgs.ZioGrpcRemoteClient.live(
            ZManagedChannel(
              ManagedChannelBuilder.forAddress(a._1, a._2.toInt).usePlaintext()
            )
          )
        }
      )
      _ <- clientSendMsgs().provideLayer(clientLayer).exitCode
    } yield ()
}

case class AppArgs(arguments: Seq[String]) extends ScallopConf(arguments) {

  import org.rogach.scallop.stringConverter

  val port = opt[String](required = false, default = Some("0"))
  val host = opt[String](required = false, default = Some("0.0.0.0"))
  verify()
}



