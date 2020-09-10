package com.tt.common.model

case class Snapshot(
                     dmpId: String,
                     countries: Map[String, Int], // a map of country -> seen count (record count)
                     countrySeenTime: Map[String, Int], // a map of country -> last seen time of country
                     cities: Option[Map[String, Int]], // an optional map of city -> seen count
                     citySeenTime: Option[Map[String, Int]], // an optional map of city -> last seen time
                     genders: Option[Map[Int, Int]], // seen count map
                     yobs: Option[Map[Int, Int]], // seen count map
                     keywords: Map[Int, Int], // keyword -> seen time count
                     keywordSeenTime: Map[Int, Int], // keyword -> last seen time
                     siteIds: Option[Map[Int, Int]], // site -> seen count
                     siteSeenTime: Option[Map[Int, Int]], // site -> last seen time
                     pageViews: Long, // lifetime pageviews
                     firstSeen: Int, // historically first seen time
                     lastSeen: Int // last seen time
                   )

object Snapshot {
  def empty(dmpId: String): Snapshot = Snapshot(
    dmpId,
    Map(),
    Map(),
    None,
    None,
    None,
    None,
    Map(),
    Map(),
    None,
    None,
    0,
    Int.MaxValue,
    0
  )
}