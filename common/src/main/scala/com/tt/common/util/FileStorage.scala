package com.tt.common.util

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

import scala.reflect.ClassTag

trait FileStorage {
  def saveRDD[T: ClassTag](rdd: RDD[T], path: String): Unit

  def loadRDD[T](path: String, minPartitions: Int = 1)
                 (implicit sparkContext: SparkContext, ct: ClassTag[T]): RDD[T]
}
