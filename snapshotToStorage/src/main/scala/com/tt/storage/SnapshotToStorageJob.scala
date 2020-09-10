package com.tt.storage

import com.tt.common.model.Snapshot
import com.tt.common.util.{FileStorage, SparkMaster}
import com.tt.storage.di.{FileStorageAware, SnapshotSaverAware, SparkSessionAware}
import com.tt.storage.util.JdbcConfig
import org.apache.spark.sql.SparkSession

class SnapshotToStorageApp(snapshotSaver: SnapshotSaver, fileStorage: FileStorage) {
  def run(inputFileName: String)
         (implicit sparkSession: SparkSession): Unit = {

    implicit val sparkContext = sparkSession.sparkContext

    val snapshots = fileStorage.loadRDD[Snapshot](inputFileName)

    snapshotSaver.store(snapshots)
  }
}

object SnapshotToStorageJob extends SnapshotSaverAware with SparkSessionAware with FileStorageAware {

  def main(args: Array[String]): Unit = {
    if (args.length != 3) {
      println("3 Arguments required [SparkMasterAddress] [InputFileName] [mysqlHost:mysqlPort/dbname,login, password]")
      return
    }

    implicit val sparkMaster: SparkMaster = SparkMaster(args(0))
    val inputFileName = args(1)
    implicit val mysqlConfig = args(2).split(",").toList match {
      case hostPostDb :: login :: password :: Nil =>
        new JdbcConfig(s"jdbc:mysql://$hostPostDb", login, password, "com.mysql.cj.jdbc.Driver")
      case _ => throw new Exception("invalid mysql config")
    }

    implicit val session = sparkSession

    val app = new SnapshotToStorageApp(snapshotSaver, fileStorage)

    app.run(inputFileName)
  }
}
