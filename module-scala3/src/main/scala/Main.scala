import app.zio.grpc.remote.clientMsgs.ZioClientMsgs.ZZioGrpcRemote
import com.typesafe.config.*
import org.rogach.scallop.ScallopConf
import io.grpc.{ServerBuilder, Status}
import scalapb.zio_grpc.{Server, ServerLayer, ServerMain, ServiceList}
import zio.{ZIO, *}
import zio.Console.*
import app.zio.grpc.remote.clientMsgs.{ZioMsgTestReply, *}
import io.grpc.protobuf.services.ProtoReflectionService


//!!Copied some code from ServerMain oblect
object ModuleScalaThree extends zio.ZIOAppDefault {
  def services: ServiceList[Any] = ServiceList.add(app.zio.server.ModuleScalaThreeServerImplementation)

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

  val myAppLogic = getArgs.flatMap(
    argsZio => parseArgs(argsZio.toList)
  ).flatMap(
    a => {
      println("host is " + a._1 + " port is " + a._2)

      val builder = ServerBuilder.forPort(a._2.toInt).addService(ProtoReflectionService.newInstance())
      ServerLayer.fromServiceList(builder, services).build
    }
  ).flatMap(_ => ZIO.never)

  def run = myAppLogic.exitCode
}

case class AppArgs(arguments: Seq[String]) extends ScallopConf(arguments) {

  import org.rogach.scallop.stringConverter

  val port = opt[String](required = false, default = Some("0"))
  val host = opt[String](required = false, default = Some("0.0.0.0"))
  verify()
}
