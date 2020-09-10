import sbt._

object Version {
  val spark = "3.0.0"

  val sparkTest = "2.4.5_0.14.0"
  val scalaTest = "3.2.2"
  val scalaMock = "4.4.0"

  val mysqlConnectJava = "8.0.21"

  val slick = "3.3.2"
  val slf4jNop = "1.6.4"

  val h2Driver = "1.4.200"
}

object Libraries {
  val sparkCore = "org.apache.spark" %% "spark-core" % Version.spark
  val sparkSql = "org.apache.spark" %% "spark-sql" % Version.spark
  val sparkTest = "com.holdenkarau" %% "spark-testing-base" %  Version.sparkTest

  val mysqlConnectJava = "mysql" % "mysql-connector-java" % Version.mysqlConnectJava

  val scalaTest = "org.scalatest" %% "scalatest" % Version.scalaTest
  val scalaMock = "org.scalamock" %% "scalamock" % Version.scalaMock

  val slick = "com.typesafe.slick" %% "slick" % Version.slick
  val slickTest = "com.typesafe.slick" %% "slick-testkit" % Version.slick
  val h2Driver = "com.h2database" % "h2" % Version.h2Driver


  val slf4jNop = "org.slf4j" % "slf4j-nop" % Version.slf4jNop
}

object Dependencies {
  import Libraries._

  val spark = Seq(sparkCore, sparkSql).map(_ % Provided)

  val sparkTests = Seq(sparkTest).map(_ % Test)
  val commonTests = Seq(scalaTest, scalaMock).map(_ % Test)

  val allTests = sparkTests ++ commonTests

  val db = Seq(mysqlConnectJava)

  val slick = Seq(Libraries.slick)
  val slickTest = Seq(Libraries.slickTest, Libraries.h2Driver).map(_ % Test)

  val logging = Seq(slf4jNop)
}
