package com.tt.analyzer.di

import com.tt.analyzer.db.{Schema, StatRepository}
import com.tt.analyzer.service.StatService
import com.tt.analyzer.service.impl.StatServiceImpl
import slick.jdbc.JdbcBackend.Database

private object DI {
  private val jdbcProfile= slick.jdbc.MySQLProfile
  private val db = Database.forConfig("mysqlCfg")
  private val schema = new {} with Schema {
    override val profile = jdbcProfile
  }

  private val statRepository = new StatRepository(jdbcProfile, db, schema)

  val statService: StatService = new StatServiceImpl(statRepository)
}
