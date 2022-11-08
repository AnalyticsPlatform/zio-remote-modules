import com.typesafe.config.*
import org.rogach.scallop.ScallopConf

import scala.sys.exit
import io.grpc.ManagedChannelBuilder
import zio.Console.*
import scalapb.zio_grpc.ZManagedChannel
import zio.*

import app.zio.grpc.remote.clientMsgs.*

object ClientApp extends ZIOAppDefault {
  //
  //  //
  //  //  def main(args: Array[String]): Unit =
  //  //    import org.rogach.scallop.ScallopConfBase
  //  //
  //  //    val appArgs = AppArgs(args)
  //  //    val host: String = appArgs.host.toOption.get
  //  //    val port: String = PortOperations.isPortAvailable(
  //  //      appArgs.port.toOption.get.toInt
  //  //    ).toString
  //  //
  //  //    try {
  //  //      val config = ConfigFactory.load(
  //  //        ConfigFactory.parseString(
  //  //          s"""
  //  //            |akka {
  //  //            | remote.classic {
  //  //            |   netty.tcp {
  //  //            |     hostname = ${host}
  //  //            |     port = $port
  //  //            |   }
  //  //            | }
  //  //            |}
  //  //            |""".stripMargin
  //  //        ).withFallback(ConfigFactory.load())
  //  //      )
  //  //      println(s"Starting actor system on $host:$port")
  //  //      val system: ActorSystem = ActorSystem("client-akka", config)
  //  //      system.actorOf(RunMainActor.props(), "RunMainActor") ! StartWork()
  //  //    } catch {
  //  //      case ex: Exception => println("ClientApp error: " + ex.getMessage)
  //  //    }
  //
  //  def myAppLogic =
  //    for {
  //      r <- ZioGrpcRemoteClient.sendZioMsgTest1(Zio)
  //      _ <- printLine(r.message)
  //    } yield ()

  def parseArgs(args: List[String]): ZIO[Any, Throwable, (String, String)] = {
    try {
      import org.rogach.scallop.ScallopConfBase
      val appArgs = AppArgs(args)
      val host: String = appArgs.host.toOption.get
      val port: String = PortOperations.isPortAvailable(
        appArgs.port.toOption.get.toInt
      ).toString
      ZIO.succeed((host, port))
    } catch {
      case e: Throwable => ZIO.fail(e)
    }
  }

  //  val clientLayer: Layer[Throwable, ZioGrpcRemoteClient] = ZioGrpcRemoteClient.live(
  //    ZManagedChannel(
  //      ManagedChannelBuilder.forAddress("localhost", 9000).usePlaintext()
  //    )
  //  )

  def clientSendMsgs: ZIO[Any, Throwable, Unit] =
    for {
      _ <- printLine("TO-DO Send msgs here")
    } yield ()

  override def run =
    for {
      args <- getArgs
      clientLayer <- parseArgs(args.toList).map(
        a => {
          println("host is " + a._1 + " port is " + a._2)
          ZioClientMsgs.ZioGrpcRemoteClient.live(
            ZManagedChannel(
              ManagedChannelBuilder.forAddress(a._1, a._2.toInt).usePlaintext()
            )
          )
        }
      )
      _ <- clientSendMsgs.provideLayer(clientLayer).exitCode
    } yield ()


  //  override def run() = myAppLogic.provideLayer(clientLayer).exitCode


}

case class AppArgs(arguments: Seq[String]) extends ScallopConf(arguments) {

  import org.rogach.scallop.stringConverter

  val port = opt[String](required = false, default = Some("0"))
  val host = opt[String](required = false, default = Some("0.0.0.0"))
  verify()
}



