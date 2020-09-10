package com.tt.analyzer.db

import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfter
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import slick.jdbc.JdbcBackend.Database
import com.tt.common.util.Constants._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.Random

class StatRepositoryTest extends AnyWordSpec with MockFactory with BeforeAndAfter {
  private val db = Database.forConfig("testCfg")

  private val jdbcProfile = slick.jdbc.MySQLProfile

  import jdbcProfile.api._

  private val schema = new {} with Schema {
    override val profile = jdbcProfile
  }

  private val repository = new StatRepository(jdbcProfile, db, schema)

  private val random = new Random()

  before {
    result(db.run(schema.schema.createIfNotExists))
  }

  after {
    result(db.run(schema.schema.dropIfExists))
  }

  "usersVisitedSiteCount" should {
    "account only people with dmp, site, gender, country" in {
      val dmpId = 1
      val country = "Lithuania"
      val countryId = 1
      val siteId = 37

      val dmpId2 = 2
      val dmpId3 = 3
      val dmpId4 = 4
      val dmpId5 = 5
      val country2 = "UK"
      val countryId2 = 2
      val siteId2 = 38

      result(db.run(DBIO.seq(
        schema.countryTQ += dto.Country(countryId, country),
        schema.countryTQ += dto.Country(countryId2, country2),

        // exact match
        schema.dmpTQ += dto.Dmp(s"$dmpId", dmpId),
        schema.snapshotStatTQ += dto.SnapshotStat(dmpId, 1, 1, 1),
        schema.genderStatTQ += dto.GenderStat(dmpId, GENDER_MALE, 1),
        schema.countryStatTQ += dto.CountryStat(dmpId, countryId, 1, 1),
        schema.siteStatTQ += dto.SiteStat(dmpId, siteId, 1, 1),

        // exact match 2
        schema.dmpTQ += dto.Dmp(s"$dmpId2", dmpId2),
        schema.snapshotStatTQ += dto.SnapshotStat(dmpId2, 1, 1, 1),
        schema.genderStatTQ += dto.GenderStat(dmpId2, GENDER_MALE, 1),
        schema.countryStatTQ += dto.CountryStat(dmpId2, countryId, 1, 1),
        schema.siteStatTQ += dto.SiteStat(dmpId2, siteId, 1, 1),

        // with other gender
        schema.dmpTQ += dto.Dmp(s"$dmpId3", dmpId3),
        schema.snapshotStatTQ += dto.SnapshotStat(dmpId3, 1, 1, 1),
        schema.genderStatTQ += dto.GenderStat(dmpId3, GENDER_FEMALE, 1),
        schema.countryStatTQ += dto.CountryStat(dmpId3, countryId, 1, 1),
        schema.siteStatTQ += dto.SiteStat(dmpId3, siteId, 1, 1),

        // with other country
        schema.dmpTQ += dto.Dmp(s"$dmpId4", dmpId4),
        schema.snapshotStatTQ += dto.SnapshotStat(dmpId4, 1, 1, 1),
        schema.genderStatTQ += dto.GenderStat(dmpId4, GENDER_MALE, 1),
        schema.countryStatTQ += dto.CountryStat(dmpId4, countryId2, 1, 1),
        schema.siteStatTQ += dto.SiteStat(dmpId4, siteId, 1, 1),

        // with other site
        schema.dmpTQ += dto.Dmp(s"$dmpId5", dmpId5),
        schema.snapshotStatTQ += dto.SnapshotStat(dmpId5, 1, 1, 1),
        schema.genderStatTQ += dto.GenderStat(dmpId5, GENDER_MALE, 1),
        schema.countryStatTQ += dto.CountryStat(dmpId5, countryId, 1, 1),
        schema.siteStatTQ += dto.SiteStat(dmpId5, siteId2, 1, 1),
      )))

      result(repository.usersVisitedSiteCount(siteId, country,  GENDER_MALE)) shouldBe 2
    }
  }

  "mostVisitedSites" should {
    "account sum seenCount and limit" in {
      val anyLastSeen = 1000
      def anyDmpId = random.nextInt().abs + 100

      val dmpId1 = 10
      val dmpId2 = 11

      val siteId1 = 1
      val siteId2 = 2
      val siteId3 = 3
      val siteId4 = 4

      result(db.run(DBIO.seq(
        schema.siteStatTQ += dto.SiteStat(dmpId1, siteId1, 1, anyLastSeen),
        schema.siteStatTQ += dto.SiteStat(dmpId1, siteId1, 3, anyLastSeen),

        schema.siteStatTQ += dto.SiteStat(dmpId2, siteId1, 1, anyLastSeen),
        schema.siteStatTQ += dto.SiteStat(dmpId2, siteId1, 2, anyLastSeen),
        schema.siteStatTQ += dto.SiteStat(dmpId2, siteId2, 8, anyLastSeen),

        schema.siteStatTQ += dto.SiteStat(anyDmpId, siteId3, 20, anyLastSeen),

        schema.siteStatTQ += dto.SiteStat(anyDmpId, siteId4, 1, anyLastSeen),
      )))

      result(repository.mostVisitedSites(3)) shouldBe Seq(
        (siteId3, 20),
        (siteId2, 8),
        (siteId1, 7)
      )
    }
  }

  "mostlyUsedKeywords" should {
    "account sum seenCount and limit" in {
      val anyLastSeen = 1000
      def anyDmpId = random.nextInt().abs + 100

      val dmpId1 = 10
      val dmpId2 = 11

      val keywordId1 = 1
      val keywordId2 = 2
      val keywordId3 = 3
      val keywordId4 = 4

      result(db.run(DBIO.seq(
        schema.keywordStatTQ += dto.KeywordStat(dmpId1, keywordId1, 1, anyLastSeen),
        schema.keywordStatTQ += dto.KeywordStat(dmpId1, keywordId1, 3, anyLastSeen),

        schema.keywordStatTQ += dto.KeywordStat(dmpId2, keywordId1, 1, anyLastSeen),
        schema.keywordStatTQ += dto.KeywordStat(dmpId2, keywordId1, 2, anyLastSeen),
        schema.keywordStatTQ += dto.KeywordStat(dmpId2, keywordId2, 8, anyLastSeen),

        schema.keywordStatTQ += dto.KeywordStat(anyDmpId, keywordId3, 20, anyLastSeen),

        schema.keywordStatTQ += dto.KeywordStat(anyDmpId, keywordId4, 1, anyLastSeen),
      )))

      result(repository.mostlyUsedKeywords(3)) shouldBe Seq(
        (keywordId3, 20),
        (keywordId2, 8),
        (keywordId1, 7)
      )
    }
  }


  "usersWhoDidNotVisitSiteCount" should {
    "account only people with dmp, site, gender, country" in {

      val anySeenCount = 1

      val dmpId1 = 1
      val dmpId2 = 2
      val dmpId3 = 3
      val dmpId4 = 4
      val dmpId5 = 5

      val siteId1 = 10
      val siteId2 = 11

      val lastSeenMatch = 11
      val lastSeenNotMatch = 8
      val fromTimestamp = 10

      result(db.run(DBIO.seq(
        // visited
        schema.dmpTQ += dto.Dmp(s"$dmpId1", dmpId1),
        schema.siteStatTQ += dto.SiteStat(dmpId1, siteId1, anySeenCount, lastSeenMatch),

        // visited this and another site
        schema.dmpTQ += dto.Dmp(s"$dmpId2", dmpId2),
        schema.siteStatTQ += dto.SiteStat(dmpId2, siteId1, anySeenCount, lastSeenMatch),
        schema.siteStatTQ += dto.SiteStat(dmpId2, siteId2, anySeenCount, lastSeenMatch),

        // visited another site
        schema.dmpTQ += dto.Dmp(s"$dmpId3", dmpId3),
        schema.siteStatTQ += dto.SiteStat(dmpId3, siteId2, anySeenCount, lastSeenMatch),

        // visited in incorrect date
        schema.dmpTQ += dto.Dmp(s"$dmpId4", dmpId4),
        schema.siteStatTQ += dto.SiteStat(dmpId4, siteId1, anySeenCount, lastSeenNotMatch),

        // visited another site in incorrect date
        schema.dmpTQ += dto.Dmp(s"$dmpId5", dmpId5),
        schema.siteStatTQ += dto.SiteStat(dmpId5, siteId2, anySeenCount, lastSeenNotMatch),
      )))

      result(repository.usersWhoDidNotVisitSiteCount(siteId1, fromTimestamp)) shouldBe 3
    }
  }
  def result[T](f: Future[T]): T = {
    Await.result(f, 10.seconds)
  }

}
