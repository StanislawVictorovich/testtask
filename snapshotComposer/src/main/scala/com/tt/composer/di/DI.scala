package com.tt.composer.di

import com.tt.common.util.impl.KryoFileStorage
import com.tt.composer.SnapshotComposer
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession

private object DI {
  val snapshotComposer: SnapshotComposer = new SnapshotComposer

  val appName = "SnapshotMergerJob"

  def sparkSession(sparkMaster: String): SparkSession = SparkSession
    .builder
    .appName(appName)
    .master(sparkMaster)
    .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    .getOrCreate()

  def sparkContext(sparkMaster: String): SparkContext = sparkSession(sparkMaster).sparkContext

  val fileStorage = new KryoFileStorage
}
