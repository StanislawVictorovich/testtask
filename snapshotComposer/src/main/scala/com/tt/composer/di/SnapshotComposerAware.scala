package com.tt.composer.di

import com.tt.composer.SnapshotComposer

class SnapshotComposerAware {
  lazy val composer: SnapshotComposer = DI.snapshotComposer
}
