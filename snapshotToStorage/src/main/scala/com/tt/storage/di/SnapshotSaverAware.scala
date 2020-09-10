package com.tt.storage.di

import com.tt.storage.SnapshotSaver
import com.tt.storage.util.JdbcConfig

trait SnapshotSaverAware {
  def snapshotSaver(implicit jdbcConfig: JdbcConfig): SnapshotSaver = DI.snapshotSaver
}
