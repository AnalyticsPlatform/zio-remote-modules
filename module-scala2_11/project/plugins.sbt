addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "1.0.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.6")
addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.0.6")

val zioGrpcVersion = "0.6.0-test3"

libraryDependencies ++= Seq(
  "com.thesamet.scalapb.zio-grpc" % "zio-grpc-codegen_2.12" % zioGrpcVersion,
  "com.thesamet.scalapb" % "compilerplugin_2.12" % "0.11.10"
)