package com.tt.analyzer.db

package object dto {
  case class Dmp(dmpIdStr: String, dmpId: Long)

  case class Country(id: Long, name: String)

  case class City(id: Long, name: String)

  case class SnapshotStat(dmpId: Long, pageViews: Long, firstSeen: Int, lastSeen: Int)

  case class CountryStat(dmpId: Long, countryId: Long, seenCount: Int, lastSeen: Int)

  case class CityStat(dmpId: Long, cityId: Long, seenCount: Int, lastSeen: Int)

  case class GenderStat(dmpId: Long, genderId: Int, seenCount: Int)

  case class YobStat(dmpId: Long, yob: Int, seenCount: Int)

  case class KeywordStat(dmpId: Long, keywordId: Int, seenCount: Int, lastSeen: Int)

  case class SiteStat(dmpId: Long, siteId: Int, seenCount: Int, lastSeen: Int)
}
