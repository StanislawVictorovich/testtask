package com.tt.generator

import com.tt.common.model.VisitLog
import com.tt.common.util.{FileStorage, SparkMaster}
import com.tt.generator.di.{FileStorageAware, SparkContextAware, VisitLogGeneratorAware}
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

case class Item(id: Int, num: Int)

class VisitLogGeneratorApp(visitLogGenerator: VisitLogGenerator, fileStorage: FileStorage) {
  def run(outputFileName: String, dmpCount: Int, eachDmpRecords: Int)(implicit context: SparkContext): Unit = {
    val generatedVisitLogs = generateVisitLogs(dmpCount, eachDmpRecords)

    fileStorage.saveRDD(generatedVisitLogs, outputFileName)
  }

  private def generateVisitLogs(dmpCount: Int, eachDmpRecords: Int)(implicit context: SparkContext): RDD[VisitLog] = {
    val dmpIds = context.parallelize(1 until dmpCount)

    val generator = context.broadcast(visitLogGenerator)

    dmpIds.flatMap {
      dmpId => (1 until eachDmpRecords).map{
        _ => generator.value.generate(dmpId)
      }
    }
  }
}

object VisitLogGeneratorJob extends VisitLogGeneratorAware with SparkContextAware with FileStorageAware {
  def main(args: Array[String]): Unit = {
    if (args.length != 4) {
      println("4 Arguments required: [SparkMasterAddress] [OutputFileName] [DmpCount] [EachDmpRecords]")
      return
    }

    implicit val sparkMaster: SparkMaster = SparkMaster(args(0))
    val outputFileName = args(1)
    val dmpCount = args(2).toInt.abs
    val eachDmpRecords = args(3).toInt.abs

    val app = new VisitLogGeneratorApp(generator, fileStorage)

    app.run(outputFileName, dmpCount, eachDmpRecords)
  }

}