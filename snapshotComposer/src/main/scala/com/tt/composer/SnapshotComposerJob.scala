package com.tt.composer

import com.tt.common.model.VisitLog
import com.tt.common.util.{FileStorage, SparkMaster}
import com.tt.composer.di.{FileStorageAware, SnapshotComposerAware, SparkContextAware}
import org.apache.spark.SparkContext

class SnapshotComposerApp(snapshotComposer: SnapshotComposer, fileStorage: FileStorage) {
  def run(inputFileName: String, outputFileName: String)(implicit context: SparkContext): Unit = {
    val visitLogs = fileStorage.loadRDD[VisitLog](inputFileName)

    val snapshots = snapshotComposer.compose(visitLogs)

    fileStorage.saveRDD(snapshots, outputFileName)
  }
}

object SnapshotComposerJob extends SnapshotComposerAware with SparkContextAware with FileStorageAware {

  def main(args: Array[String]): Unit = {
    if (args.length != 3) {
      println("3 Arguments required [SparkMasterAddress] [InputFileName] [OutputFileName]")
      return
    }

    implicit val sparkMaster: SparkMaster = SparkMaster(args(0))
    val inputFileName = args(1)
    val outputFileName = args(2)

    val app = new SnapshotComposerApp(composer, fileStorage)

    app.run(inputFileName, outputFileName)
  }
}
