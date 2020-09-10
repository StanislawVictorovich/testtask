package com.tt.storage.di

import com.tt.common.util.SparkMaster
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession

trait SparkSessionAware {
  def sparkSession(implicit master: SparkMaster): SparkSession = DI.sparkSession(master.address)
}
