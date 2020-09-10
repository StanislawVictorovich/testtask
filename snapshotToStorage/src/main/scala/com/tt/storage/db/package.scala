package com.tt.storage

import org.apache.spark.sql.TypedColumn

package object db {


  abstract class TableModel(val tableName: String, val colNames: Seq[String])
  abstract class ReadTableModel(val tableName: String, val typedColumns: Seq[TypedColumn[Any, Any]])

  case class DmpIdentifiable(dmpIdStr: String, dmpId: Long)
  object DmpIdentifiable extends TableModel("dmp", Seq("dmp_id_str", "dmp_id"))

  case class CountryIdentifiable(id: Long, name: String)
  object CountryIdentifiable extends TableModel("country", Seq("id", "name"))

  case class CityIdentifiable(id: Long, name: String)
  object CityIdentifiable extends TableModel("city", Seq("id", "name"))



  case class SnapshotStat(dmpId: Long, pageViews: Long, firstSeen: Int, lastSeen: Int)
  object SnapshotStat extends TableModel("snapshot", Seq("dmp_id", "page_views", "first_seen", "last_seen"))

  case class CountryStat(dmpId: Long, countryId: Long, seenCount: Int, lastSeen: Int)
  object CountryStat extends TableModel("country_stat", Seq("dmp_id", "country_id", "seen_count", "last_seen"))

  case class CityStat(dmpId: Long, cityId: Long, seenCount: Int, lastSeen: Int)
  object CityStat extends TableModel("city_stat", Seq("dmp_id", "city_id", "seen_count", "last_seen"))


  case class GenderStat(dmpId: Long, genderId: Int, seenCount: Int)
  object GenderStat extends TableModel("gender_stat", Seq("dmp_id", "gender_id", "seen_count"))


  case class YobStat(dmpId: Long, yob: Int, seenCount: Int)
  object YobStat extends TableModel("yob_stat", Seq("dmp_id", "yob", "seen_count"))


  case class KeywordStat(dmpId: Long, keywordId: Int, seenCount: Int, lastSeen: Int)
  object KeywordStat extends TableModel("keyword_stat", Seq("dmp_id", "keyword_id", "seen_count", "last_seen"))


  case class SiteStat(dmpId: Long, siteId: Int, seenCount: Int, lastSeen: Int)
  object SiteStat extends TableModel("site_stat", Seq("dmp_id", "site_id", "seen_count", "last_seen"))
}
