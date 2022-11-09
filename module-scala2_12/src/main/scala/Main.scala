import com.typesafe.config._
import io.grpc.ServerBuilder
import io.grpc.protobuf.services.ProtoReflectionService
import org.rogach.scallop.ScallopConf
import scalapb.zio_grpc.{ServerLayer, ServiceList}
import zio.ZIO


object ModuleScala2_12 extends zio.ZIOAppDefault {
  def services: ServiceList[Any] = ServiceList.add(app.zio.server.ModuleScala2_12ServerImplementation)

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
  val port = opt[String](required = false, default = Some("0"))
  val host = opt[String](required = false, default = Some("0.0.0.0"))
  verify()
}
