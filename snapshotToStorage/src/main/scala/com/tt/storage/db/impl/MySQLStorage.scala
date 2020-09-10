package com.tt.storage.db.impl

import com.tt.storage.db
import com.tt.storage.db.Storage
import com.tt.storage.util.JdbcConfig
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Encoder, SaveMode, SparkSession}

class MySQLStorage(jdbcConfig: JdbcConfig) extends Storage {
  //Class.forName("com.mysql.cj.jdbc.Driver")
  //val jdbcConfig = new JdbcConfig("jdbc:mysql://mysql:3306/month_snapshot", "root", "rOOt", "com.mysql.cj.jdbc.Driver")

  override def save[T: Encoder](rdd: RDD[T], tableModel: db.TableModel)(implicit sparkSession: SparkSession): Unit = {
    import sparkSession.sqlContext.implicits._

    rdd.toDF(tableModel.colNames: _*)
      .write
      .mode(SaveMode.Append)
      .jdbc(jdbcConfig.url, tableModel.tableName, jdbcConfig.props)
  }
}