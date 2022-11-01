run / fork := true

name := "module-scala2_11"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.12"

enablePlugins(JavaAppPackaging)

Compile / PB.targets := Seq(
  scalapb.gen(grpc = true) -> (Compile / sourceManaged).value,
  scalapb.zio_grpc.ZioCodeGenerator -> (Compile / sourceManaged).value
)

libraryDependencies ++= {
  val vScallop = "3.4.0"
  val grpcVersion = "1.47.0"
  Seq(
    "org.rogach" %% "scallop" % vScallop,
    "io.grpc" % "grpc-netty" % grpcVersion,
    "com.thesamet.scalapb" % "scalapb-runtime-grpc_2.12" % scalapb.compiler.Version.scalapbVersion,
    "com.typesafe" % "config" % "1.4.2"
  )
}