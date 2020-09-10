package com.tt.analyzer.db

import slick.jdbc.JdbcProfile

trait Schema {

  val profile: JdbcProfile

  import profile.api._

  class Dmp(tag: Tag) extends Table[dto.Dmp](tag, "dmp") {

    def dmpIdStr = column[String]("dmp_id_str", O.PrimaryKey)
    def dmpId = column[Long]("dmp_id")

    def * = (dmpIdStr, dmpId) <> (dto.Dmp.tupled, dto.Dmp.unapply)
  }

  val dmpTQ = TableQuery[Dmp]

  class Country(tag: Tag) extends Table[dto.Country](tag, "country") {

    def id = column[Long]("id", O.PrimaryKey)
    def name = column[String]("name")

    def * = (id, name) <> (dto.Country.tupled, dto.Country.unapply)
  }

  val countryTQ = TableQuery[Country]

  class City(tag: Tag) extends Table[dto.City](tag, "city") {

    def id = column[Long]("id", O.PrimaryKey)
    def name = column[String]("name")

    
    def * = (id, name) <> (dto.City.tupled, dto.City.unapply)
  }

  val cityTQ = TableQuery[City]

  class SnapshotStat(tag: Tag) extends Table[dto.SnapshotStat](tag, "snapshot") {

    def dmpId = column[Long]("dmp_id")
    //def dmpIdFk = foreignKey("dmp_id_fk", dmpId, dmpTQ)(_.dmpId)
    def pageViews = column[Long]("page_views")
    def firstSeen = column[Int]("first_seen")
    def lastSeen = column[Int]("last_seen")

    def * = (dmpId, pageViews, firstSeen, lastSeen) <> (dto.SnapshotStat.tupled, dto.SnapshotStat.unapply)
  }

  val snapshotStatTQ = TableQuery[SnapshotStat]

  class CountryStat(tag: Tag) extends Table[dto.CountryStat](tag, "country_stat") {

    def dmpId = column[Long]("dmp_id")

    def countryId = column[Long]("country_id")

    def seenCount = column[Int]("seen_count")
    def lastSeen = column[Int]("last_seen")

    def * = (dmpId, countryId, seenCount, lastSeen) <> (dto.CountryStat.tupled, dto.CountryStat.unapply)
  }

  val countryStatTQ = TableQuery[CountryStat]

  class CityStat(tag: Tag) extends Table[dto.CityStat](tag, "city_stat") {

    def dmpId = column[Long]("dmp_id")

    def cityId = column[Long]("city_id")

    def seenCount = column[Int]("seen_count")
    def lastSeen = column[Int]("last_seen")

    def * = (dmpId, cityId, seenCount, lastSeen) <> (dto.CityStat.tupled, dto.CityStat.unapply)
  }

  val cityStatTQ = TableQuery[CityStat]

  class GenderStat(tag: Tag) extends Table[dto.GenderStat](tag, "gender_stat") {

    def dmpId = column[Long]("dmp_id")

    def genderId = column[Int]("gender_id")

    def seenCount = column[Int]("seen_count")

    def * = (dmpId, genderId, seenCount) <> (dto.GenderStat.tupled, dto.GenderStat.unapply)
  }

  val genderStatTQ = TableQuery[GenderStat]

  class YobStat(tag: Tag) extends Table[dto.YobStat](tag, "yob_stat") {

    def dmpId = column[Long]("dmp_id")

    def yob = column[Int]("yob")

    def seenCount = column[Int]("seen_count")

    def * = (dmpId, yob, seenCount) <> (dto.YobStat.tupled, dto.YobStat.unapply)
  }

  val yobStatTQ = TableQuery[YobStat]


  class KeywordStat(tag: Tag) extends Table[dto.KeywordStat](tag, "keyword_stat") {

    def dmpId = column[Long]("dmp_id")

    def keywordId = column[Int]("keyword_id")

    def seenCount = column[Int]("seen_count")
    def lastSeen = column[Int]("last_seen")

    def * = (dmpId, keywordId, seenCount, lastSeen) <> (dto.KeywordStat.tupled, dto.KeywordStat.unapply)
  }

  val keywordStatTQ = TableQuery[KeywordStat]

  class SiteStat(tag: Tag) extends Table[dto.SiteStat](tag, "site_stat") {

    def dmpId = column[Long]("dmp_id")

    def siteId = column[Int]("site_id")

    def seenCount = column[Int]("seen_count")
    def lastSeen = column[Int]("last_seen")

    def * = (dmpId, siteId, seenCount, lastSeen) <> (dto.SiteStat.tupled, dto.SiteStat.unapply)
  }

  val siteStatTQ = TableQuery[SiteStat]

  lazy val schema = dmpTQ.schema ++
    countryTQ.schema ++
    cityTQ.schema ++
    snapshotStatTQ.schema ++
    countryStatTQ.schema ++
    cityStatTQ.schema ++
    genderStatTQ.schema ++
    yobStatTQ.schema ++
    keywordStatTQ.schema ++
    siteStatTQ.schema
}
