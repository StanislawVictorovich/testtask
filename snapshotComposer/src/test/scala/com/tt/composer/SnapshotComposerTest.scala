package com.tt.composer

import com.holdenkarau.spark.testing.{RDDComparisons, SharedSparkContext}
import com.tt.common.model.{Snapshot, VisitLog}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class SnapshotComposerTest extends AnyWordSpec with SharedSparkContext with RDDComparisons {


  "compose" should {
    "correctly consider different dmpIds" in {
      val singleVisitLog = sc.parallelize(Seq(
        VisitLog(
          "dmpId1",
          "country1",
          utcDays = 11
        ),
        VisitLog(
          "dmpId1",
          "country1",
          utcDays = 22
        ),
        VisitLog(
          "dmpId2",
          "country1",
          utcDays = 33
        )
      ))

      val expectedSnapshots = Seq(
        emptySnapshot.copy(
          dmpId = "dmpId1",
          countries = Map("country1" -> 2),
          countrySeenTime = Map("country1" -> 22),
          pageViews = 2,
          firstSeen = 11,
          lastSeen = 22
        ),
        emptySnapshot.copy(
          dmpId = "dmpId2",
          countries = Map("country1" -> 1),
          countrySeenTime = Map("country1" -> 33),
          pageViews = 1,
          firstSeen = 33,
          lastSeen = 33
        )
      )

      composer.compose(singleVisitLog).collect() should contain theSameElementsAs expectedSnapshots
    }

    "compose snapshots according to spec" in {

      val dmpId1 = "dmpId1"
      val dmpId2 = "dmpId2"
      val dmpId3 = "dmpId3"
      val gender1 = 100
      val gender2 = 101
      val country1 = "country1"
      val country2 = "country2"
      val country1city1 = "country1city1"
      val country1city2 = "country1city2"
      val yob1 = 2000
      val yob2 = 2001
      val keyword1 = 1001
      val keyword2 = 1002
      val keyword3 = 1003
      val site1 = 3001
      val site2 = 3002
      val timestampMin = 10001
      val timestampMid = 10002
      val timestampMax = 10003

      val singleVisitLog = sc.parallelize(Seq(
        VisitLog(
          dmpId1,
          country1,
          Some(country1city1),
          Some(gender1),
          Some(yob1),
          Some(List(keyword1, keyword2)),
          Some(site1),
          timestampMid
        ),
        VisitLog(
          dmpId1,
          country1,
          Some(country1city2),
          Some(gender2),
          Some(yob2),
          Some(List(keyword2, keyword3)),
          Some(site2),
          timestampMax
        ),
        VisitLog(
          dmpId1,
          country1,
          None,
          None,
          None,
          None,
          None,
          timestampMid
        ),
        VisitLog(
          dmpId1,
          country1,
          Some(country1city1),
          Some(gender1),
          Some(yob1),
          Some(List(keyword1, keyword2)),
          Some(site1),
          timestampMin
        ),
        VisitLog(
          dmpId1,
          country2,
          None,
          None,
          None,
          None,
          None,
          timestampMid
        ),
        // different user with filled data
        VisitLog(
          dmpId2,
          country1,
          Some(country1city2),
          Some(gender2),
          Some(yob2),
          Some(List(keyword2, keyword3)),
          Some(site2),
          timestampMax
        ),
        // different user with filled data
        VisitLog(
          dmpId3,
          country1,
          None,
          None,
          None,
          None,
          None,
          timestampMin
        ),
      ))

      val expectedSnapshots = Seq(
        Snapshot(
          dmpId = dmpId1,
          countries = Map(country1 -> 4, country2 -> 1),
          countrySeenTime = Map(country1 -> timestampMax, country2 -> timestampMid),
          cities = Some(Map(country1city1 -> 2, country1city2 -> 1)),
          citySeenTime = Some(Map(country1city1 -> timestampMid , country1city2 -> timestampMax)),
          genders = Some(Map(gender1 -> 2, gender2 -> 1)),
          yobs = Some(Map(yob1 -> 2, yob2 -> 1)),
          keywords = Map(keyword1 -> 2, keyword2 -> 3, keyword3 -> 1),
          keywordSeenTime = Map(keyword1 -> timestampMid, keyword2 -> timestampMax, keyword3 -> timestampMax),
          siteIds = Some(Map(site1 -> 2, site2 -> 1)),
          siteSeenTime = Some(Map(site1 -> timestampMid, site2 -> timestampMax)),
          pageViews = 5,
          firstSeen = timestampMin,
          lastSeen = timestampMax
        ),
        Snapshot(
          dmpId = dmpId2,
          countries = Map(country1 -> 1),
          countrySeenTime = Map(country1 -> timestampMax),
          cities = Some(Map(country1city2 -> 1)),
          citySeenTime = Some(Map(country1city2 -> timestampMax)),
          genders = Some(Map(gender2 -> 1)),
          yobs = Some(Map(yob2 -> 1)),
          keywords = Map(keyword2 -> 1, keyword3 -> 1),
          keywordSeenTime = Map(keyword2 -> timestampMax, keyword3 -> timestampMax),
          siteIds = Some(Map(site2 -> 1)),
          siteSeenTime = Some(Map(site2 -> timestampMax)),
          pageViews = 1,
          firstSeen = timestampMax,
          lastSeen = timestampMax
        ),
        Snapshot(
          dmpId = dmpId3,
          countries = Map(country1 -> 1),
          countrySeenTime = Map(country1 -> timestampMin),
          cities = None,
          citySeenTime = None,
          genders = None,
          yobs = None,
          keywords = Map(),
          keywordSeenTime = Map(),
          siteIds = None,
          siteSeenTime = None,
          pageViews = 1,
          firstSeen = timestampMin,
          lastSeen = timestampMin
        )
      )

      composer.compose(singleVisitLog).collect() should contain theSameElementsAs expectedSnapshots
    }
  }

  private def composer = {
    new SnapshotComposer
  }

  def emptySnapshot: Snapshot = Snapshot(
    "",
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
    0,
    0
  )
}
