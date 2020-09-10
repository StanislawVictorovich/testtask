package com.tt.merger

import com.tt.common.model.Snapshot
import com.tt.common.util.{FileStorage, SparkMaster}
import com.tt.merger.di.{FileStorageAware, SnapshotMergerAware, SparkContextAware}
import org.apache.spark.SparkContext

class SnapshotMergerApp(snapshotMerger: SnapshotMerger, fileStorage: FileStorage) {
  def run(inputFileName1: String, inputFileName2: String, outputFileName: String)
         (implicit context: SparkContext): Unit = {

    val snapshots1 = fileStorage.loadRDD[Snapshot](inputFileName1)
    val snapshots2 = fileStorage.loadRDD[Snapshot](inputFileName2)

    val snapshots = snapshotMerger.merge(snapshots1 ++ snapshots2)

    fileStorage.saveRDD(snapshots, outputFileName)
  }
}

object SnapshotMergerJob extends SnapshotMergerAware with SparkContextAware with FileStorageAware {
  def main(args: Array[String]): Unit = {
    if (args.length != 4) {
      println("4 Arguments required [SparkMasterAddress] [InputFileName1] [InputFileName2] [OutputFileName]")
      return
    }

    implicit val sparkMaster: SparkMaster = SparkMaster(args(0))
    val inputFileName1 = args(1)
    val inputFileName2 = args(2)
    val outputFileName = args(3)

    val app = new SnapshotMergerApp(snapshotMerger, fileStorage)

    app.run(inputFileName1, inputFileName2, outputFileName)
  }
}
