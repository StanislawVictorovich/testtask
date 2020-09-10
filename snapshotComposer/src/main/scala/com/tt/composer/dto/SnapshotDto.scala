package com.tt.composer.dto

import com.tt.common.model.Snapshot
import com.tt.common.util.CommonMap._

case class SnapshotDto(
                        dmpId: String,
                        countries: Map[String, Int], // a map of country -> seen count (record count)
                        countrySeenTime: Map[String, Int], // a map of country -> last seen time of country
                        cities: Map[String, Int], // an optional map of city -> seen count
                        citySeenTime: Map[String, Int], // an optional map of city -> last seen time
                        genders: Map[Int, Int], // seen count map
                        yobs: Map[Int, Int], // seen count map
                        keywords: Map[Int, Int], // keyword -> seen time count
                        keywordSeenTime: Map[Int, Int], // keyword -> last seen time
                        siteIds: Map[Int, Int], // site -> seen count
                        siteSeenTime: Map[Int, Int], // site -> last seen time
                        pageViews: Long, // lifetime pageviews
                        firstSeen: Int, // historically first seen time
                        lastSeen: Int // last seen time
                      ) {
  def toSnapshot: Snapshot = {
    Snapshot(
      dmpId,
      countries,
      countrySeenTime,
      cities.toOpt,
      citySeenTime.toOpt,
      genders.toOpt,
      yobs.toOpt,
      keywords,
      keywordSeenTime,
      siteIds.toOpt,
      siteSeenTime.toOpt,
      pageViews,
      firstSeen,
      lastSeen
    )
  }
}

object SnapshotDto extends Serializable {
  def emptyWithMaps(dmpId: String): SnapshotDto = SnapshotDto(
    dmpId,
    Map[String, Int](),
    Map[String, Int](),
    Map[String, Int](),
    Map[String, Int](),
    Map[Int, Int](),
    Map[Int, Int](),
    Map[Int, Int](),
    Map[Int, Int](),
    Map[Int, Int](),
    Map[Int, Int](),
    0,
    Int.MaxValue,
    0
  )
}
