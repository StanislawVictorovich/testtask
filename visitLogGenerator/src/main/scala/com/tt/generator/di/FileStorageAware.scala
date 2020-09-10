package com.tt.generator.di

import com.tt.common.util.FileStorage

trait FileStorageAware {
  lazy val fileStorage: FileStorage = DI.fileStorage
}
