import com.typesafe.config.*
import org.rogach.scallop.ScallopConf

import scala.sys.exit

//object ClientApp extends zio.ZIOAppDefault {
//
//  def main(args: Array[String]): Unit =
//    import org.rogach.scallop.ScallopConfBase
//
//    val appArgs = AppArgs(args)
//    val host: String = appArgs.host.toOption.get
//    val port: String = PortOperations.isPortAvailable(
//      appArgs.port.toOption.get.toInt
//    ).toString
//
//    try {
//      val config = ConfigFactory.load(
//        ConfigFactory.parseString(
//          s"""
//            |akka {
//            | remote.classic {
//            |   netty.tcp {
//            |     hostname = ${host}
//            |     port = $port
//            |   }
//            | }
//            |}
//            |""".stripMargin
//        ).withFallback(ConfigFactory.load())
//      )
//      println(s"Starting actor system on $host:$port")
//      val system: ActorSystem = ActorSystem("client-akka", config)
//      system.actorOf(RunMainActor.props(), "RunMainActor") ! StartWork()
//    } catch {
//      case ex: Exception => println("ClientApp error: " + ex.getMessage)
//    }
//}

case class AppArgs(arguments: Seq[String]) extends ScallopConf(arguments) {
  import org.rogach.scallop.stringConverter
  val port = opt[String](required = false, default = Some("0"))
  val host = opt[String](required = false, default = Some("0.0.0.0"))
  verify()
}



