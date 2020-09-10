package com.tt.storage.di

import com.tt.common.util.impl.KryoFileStorage
import com.tt.storage.SnapshotSaver
import com.tt.storage.db.Storage
import com.tt.storage.db.impl.MySQLStorage
import com.tt.storage.util.JdbcConfig
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession

private object DI {
  private def mysqlStorage(jdbcConfig: JdbcConfig): Storage = new MySQLStorage(jdbcConfig)

  def snapshotSaver(implicit jdbcConfig: JdbcConfig): SnapshotSaver = {
    new SnapshotSaver()(mysqlStorage(jdbcConfig))
  }

  val appName = "SnapshotToStorageJob"

  def sparkSession(sparkMaster: String): SparkSession = SparkSession
    .builder
    .appName(appName)
    .master(sparkMaster)
    .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    .getOrCreate()

  def sparkContext(sparkMaster: String): SparkContext = sparkSession(sparkMaster).sparkContext

  val fileStorage = new KryoFileStorage
}
