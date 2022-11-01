import com.typesafe.config._
import akka.actor._
import org.rogach.scallop.ScallopConf

object ModuleScala2_11Run {


  def main(args: Array[String]): Unit = {
    println("Starting akka module scala 2_11")

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
             | remote {
             |   netty.tcp {
             |     hostname = ${host}
             |     port = $port
             |   }
             | }
             |}
             |""".stripMargin
        ).withFallback(ConfigFactory.load())
      )
      println(s"Starting actor system akka module scala 2_11 on $host:$port")
      val backend = ActorSystem("backendScalaTwoEleven", config)

      val ref = backend.actorOf(SimpleActorScalaTwoEleven.apply(), "simple-scala-2-11")
      println("Path of simple actor: " + ref.path.toString)
      println(s"Successfully started akka ${backend.name} module scala 2_11")
    } catch {
      case ex: Exception => println("ModuleScalaThree error: " + ex.getMessage)
    }
  }
}

case class AppArgs(arguments: Seq[String]) extends ScallopConf(arguments) {
  val port = opt[String](required = false, default = Some("0"))
  val host = opt[String](required = false, default = Some("0.0.0.0"))
  verify()
}
