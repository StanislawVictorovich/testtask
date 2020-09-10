package com.tt.merger.di

import com.tt.common.util.FileStorage

trait FileStorageAware {
  lazy val fileStorage: FileStorage = DI.fileStorage
}
