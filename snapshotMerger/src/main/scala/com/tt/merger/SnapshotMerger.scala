package com.tt.merger

import com.tt.common.model.Snapshot
import org.apache.spark.rdd.RDD

class SnapshotMerger {
  def merge(snapshots: RDD[Snapshot]): RDD[Snapshot] = {
    import SnapshotMerger._

    snapshots
      .groupBy(_.dmpId)
      .map {
        case (dmpId, ss) =>
          ss.fold(Snapshot.empty(dmpId)) {
            case (acc: Snapshot, curr: Snapshot) =>
              acc.copy(
                countries = mergeCounters(acc.countries, curr.countries),
                countrySeenTime = mergeLastSeen(acc.countrySeenTime, curr.countrySeenTime),
                cities = mergeCounters(acc.cities, curr.cities),
                citySeenTime = mergeLastSeen(acc.citySeenTime, curr.citySeenTime),
                genders = mergeCounters(acc.genders, curr.genders),
                yobs = mergeCounters(acc.yobs, curr.yobs),
                keywords = mergeCounters(acc.keywords, curr.keywords),
                keywordSeenTime = mergeLastSeen(acc.keywordSeenTime, curr.keywordSeenTime),
                siteIds = mergeCounters(acc.siteIds, curr.siteIds),
                siteSeenTime = mergeLastSeen(acc.siteSeenTime, curr.siteSeenTime),
                pageViews = acc.pageViews + curr.pageViews,
                firstSeen = acc.firstSeen.min(curr.firstSeen),
                lastSeen = acc.lastSeen.max(curr.lastSeen)
              )
          }
      }
  }
}

object SnapshotMerger {
  private def mergeCounters[T](cm1: Map[T, Int], cm2: Map[T, Int]): Map[T, Int] = {
    cm1 ++ cm2.map {
      case (key, count) => key -> (count + cm1.getOrElse(key, 0))
    }
  }

  private def mergeCounters[T](cm1: Option[Map[T, Int]], cm2: Option[Map[T, Int]]): Option[Map[T, Int]] = {
    mergeCounters(cm1.getOrElse(Map()), cm2.getOrElse(Map())) match {
      case m if m.nonEmpty => Some(m)
      case _ => None
    }
  }

  private def mergeLastSeen[T](cm1: Map[T, Int], cm2: Map[T, Int]): Map[T, Int] = {
    cm1 ++ cm2.map {
      case (key, lastSeen) => key -> (lastSeen.max(cm1.getOrElse(key, 0)))
    }
  }

  private def mergeLastSeen[T](cm1: Option[Map[T, Int]], cm2: Option[Map[T, Int]]): Option[Map[T, Int]] = {
    mergeLastSeen(cm1.getOrElse(Map()), cm2.getOrElse(Map())) match {
      case m if m.nonEmpty => Some(m)
      case _ => None
    }
  }
}