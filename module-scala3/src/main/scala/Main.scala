import com.typesafe.config.*
import akka.actor.*
import org.rogach.scallop.ScallopConf

object ModuleScalaThree {
  def main(args: Array[String]): Unit = {
    println("Starting akka module scala 3")
    val appArgs = AppArgs(args.toSeq)
    val host: String = appArgs.host.toOption.get
    val port: String = PortOperations.isPortAvailable(
      appArgs.port.toOption.get.toInt
    ).toString

    try {
      val config = ConfigFactory.load(
        ConfigFactory.parseString(
          s"""
             |akka {
             | remote.classic {
             |   netty.tcp {
             |     hostname = ${host}
             |     port = $port
             |   }
             | }
             |}
             |""".stripMargin
        ).withFallback(ConfigFactory.load())
      )
      println(s"Starting actor system akka module scala 3 on $host:$port")
      val backend = ActorSystem("backendScalaThree", config)

      val ref = backend.actorOf(SimpleActorScalaThree.apply(), "simpleScalaThree")
      println("Path of simple actor: " + ref.path.toString)
      println(s"Successfully started akkasystem ${backend.name} module scala 3")
    } catch {
      case ex: Exception => println("ModuleScalaThree error: " + ex.getMessage)
    }
  }
}

case class AppArgs(arguments: Seq[String]) extends ScallopConf(arguments) {
  import org.rogach.scallop.stringConverter
  val port = opt[String](required = false, default = Some("0"))
  val host = opt[String](required = false, default = Some("0.0.0.0"))
  verify()
}
