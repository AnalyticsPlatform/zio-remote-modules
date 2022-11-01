enablePlugins(JavaAppPackaging)

run / fork := true

name := "module-scala3"

version := "0.1.0-SNAPSHOT"

scalaVersion := "3.2.0"

Compile / PB.targets := Seq(
  scalapb.gen(grpc = true) -> (Compile / sourceManaged).value,
  scalapb.zio_grpc.ZioCodeGenerator -> (Compile / sourceManaged).value
)

libraryDependencies ++= {
  val vScallop = "4.1.0"
  val grpcVersion = "1.47.0"
  Seq(
    "org.rogach" %% "scallop" % vScallop,
    "io.grpc" % "grpc-netty" % grpcVersion,
    "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
    "com.typesafe" % "config" % "1.4.2",
    "org.scalameta" %% "munit" % "0.7.29" % Test
  )
}