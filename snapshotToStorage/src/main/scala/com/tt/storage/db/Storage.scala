package com.tt.storage.db

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Encoder, SparkSession}

trait Storage {
  def save[T: Encoder](rdd: RDD[T], tableModel: TableModel)
             (implicit sparkSession: SparkSession): Unit
}


object Storage {
  object implicits {
    implicit class StorableRdd[T: Encoder](rdd: RDD[T]) {
      def save(tableModel: TableModel)
              (implicit storage: Storage, sparkSession: SparkSession): Unit = {
        storage.save(rdd, tableModel)
      }
    }
  }
}