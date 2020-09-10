package com.tt.composer

import com.tt.common.model.{Snapshot, VisitLog}
import com.tt.composer.dto.SnapshotDto
import org.apache.spark.rdd.RDD

class SnapshotComposer {
  def compose(visitLogs: RDD[VisitLog]): RDD[Snapshot] = {
    import SnapshotComposer._

    visitLogs
      .groupBy(_.dmpId)
      .map {
        case (dmpId, visitLogs) =>
          visitLogs.foldLeft(SnapshotDto.emptyWithMaps(dmpId)) {
            case (acc: SnapshotDto, visit: VisitLog) =>
              acc.copy(
                countries = increment(acc.countries, visit.country),
                countrySeenTime = lastSeen(acc.countrySeenTime, visit.country, visit.utcDays),
                cities = increment(acc.cities, visit.city),
                citySeenTime = lastSeen(acc.citySeenTime, visit.city, visit.utcDays),
                genders = increment(acc.genders, visit.gender),
                yobs = increment(acc.yobs, visit.yob),
                keywords = increment(acc.keywords, visit.keywords.getOrElse(List.empty)),
                keywordSeenTime = lastSeen(acc.keywordSeenTime, visit.keywords.getOrElse(List.empty), visit.utcDays),
                siteIds = increment(acc.siteIds, visit.siteId),
                siteSeenTime = lastSeen(acc.siteSeenTime, visit.siteId, visit.utcDays),
                pageViews = acc.pageViews + 1,
                firstSeen = acc.firstSeen.min(visit.utcDays),
                lastSeen = acc.lastSeen.max(visit.utcDays)
              )
          }
      }
      .map(_.toSnapshot)
  }
}

object SnapshotComposer {

  private def increment[T](m: Map[T, Int], key: T): Map[T, Int] = {
    m + (key -> (m.getOrElse(key, 0) + 1))
  }

  private def increment(m: Map[Int, Int], keys: List[Int]): Map[Int, Int] = {
    m ++ keys.map {
      key => (key, m.getOrElse(key, 0) + 1)
    }.toMap
  }

  private def increment[T](m: Map[T, Int], keyOpt: Option[T]): Map[T, Int] = {
    keyOpt match {
      case Some(key) =>
        m + (key -> (m.getOrElse(key, 0) + 1))
      case _ => m
    }
  }


  private def lastSeen[T](m: Map[T, Int], key: T, lastSeen: Int): Map[T, Int] = {
    m + (key -> m.getOrElse(key, 0).max(lastSeen))
  }

  private def lastSeen[T](m: Map[T, Int], keyOpt: Option[T], lastSeen: Int): Map[T, Int] = {
    keyOpt match {
      case Some(key) =>
        m + (key -> m.getOrElse(key, 0).max(lastSeen))
      case _ => m
    }
  }

  private def lastSeen(m: Map[Int, Int], keys: List[Int], lastSeen: Int): Map[Int, Int] = {
    m ++ keys.map {
      key => (key, m.getOrElse(key, 0).max(lastSeen))
    }.toMap
  }
}