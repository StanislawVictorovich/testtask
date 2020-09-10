package com.tt.merger.di

import com.tt.common.util.impl.KryoFileStorage
import com.tt.merger.SnapshotMerger
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession

private object DI {
  val snapshotMerger: SnapshotMerger = new SnapshotMerger

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
