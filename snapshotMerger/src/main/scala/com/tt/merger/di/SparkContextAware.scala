package com.tt.merger.di

import com.tt.common.util.SparkMaster
import org.apache.spark.SparkContext

trait SparkContextAware {
  implicit def sparkContext(implicit master: SparkMaster): SparkContext = DI.sparkContext(master.address)
}
