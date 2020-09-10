package com.tt.analyzer.di

import com.tt.analyzer.service.StatService

trait StatServiceAware {
  val statService: StatService = DI.statService
}
