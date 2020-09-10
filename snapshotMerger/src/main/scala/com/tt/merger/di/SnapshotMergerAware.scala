package com.tt.merger.di

import com.tt.merger.SnapshotMerger

class SnapshotMergerAware {
  lazy val snapshotMerger: SnapshotMerger = DI.snapshotMerger
}
