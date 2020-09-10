package com.tt.common.util.impl

import com.tt.common.util.{FileStorage, Kryo}
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

import scala.reflect.ClassTag

class KryoFileStorage extends FileStorage {
  override def saveRDD[T: ClassTag](rdd: RDD[T], path: String): Unit = {
    Kryo.saveAsObjectFile(rdd, path)
  }

  override def loadRDD[T](path: String, minPartitions: Int = 1)
                          (implicit sparkContext: SparkContext, ct: ClassTag[T]): RDD[T] = {
    Kryo.readObjectFromFile(sparkContext, path, minPartitions)
  }
}
