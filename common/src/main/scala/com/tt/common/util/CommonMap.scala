package com.tt.common.util

object CommonMap {
  implicit class OptionalMap[K, V](val initialMap: Map[K, V]) extends AnyVal {
    def toOpt: Option[Map[K, V]] = {
      if (initialMap.nonEmpty) {
        Some(initialMap)
      } else {
        None
      }
    }
  }
}