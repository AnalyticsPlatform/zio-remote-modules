enablePlugins(JavaAppPackaging)

run / fork := true

name := "module-scala2_12"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.12.15"

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
    "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
    "com.typesafe" % "config" % "1.4.2"
  )
}