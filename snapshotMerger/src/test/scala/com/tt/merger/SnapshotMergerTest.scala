package com.tt.merger

import com.holdenkarau.spark.testing.{RDDComparisons, SharedSparkContext}
import com.tt.common.model.Snapshot

import scala.util.Random
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class SnapshotMergerTest extends AnyWordSpec with SharedSparkContext with RDDComparisons {

  "merge" should {
    "correctly consider different dmpIds" in {
      val initialSnapshots = sc.parallelize(Seq(
        emptySnapshot.copy(
          dmpId = "dmpId1",
          pageViews = 1,
        ),
        emptySnapshot.copy(
          dmpId = "dmpId1",
          pageViews = 1,
        ),
        emptySnapshot.copy(
          dmpId = "dmpId2",
          pageViews = 1,
        ),
      ))

      val expectedSnapshots = Seq(
        emptySnapshot.copy(
          dmpId = "dmpId1",
          pageViews = 2,
        ),
        emptySnapshot.copy(
          dmpId = "dmpId2",
          pageViews = 1,
        ),
      )

      merger.merge(initialSnapshots).collect() should contain theSameElementsAs expectedSnapshots
    }

    "merge according to spec regardless of order" in {

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

      val random = new Random


      val snapshot1 = Snapshot(
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
      )

      val snapshot2 = Snapshot(
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

      val initialSnapshots = Seq(
        Snapshot(
          dmpId = dmpId1,
          countries = Map(country1 -> 1),
          countrySeenTime = Map(country1 -> timestampMax),
          cities = Some(Map(country1city1 -> 2)),
          citySeenTime = Some(Map(country1city1 -> timestampMid)),
          genders = Some(Map(gender1 -> 2)),
          yobs = Some(Map(yob1 -> 2)),
          keywords = Map(keyword1 -> 2, keyword2 -> 3, keyword3 -> 1),
          keywordSeenTime = Map(keyword1 -> timestampMid, keyword2 -> timestampMax, keyword3 -> timestampMin),
          siteIds = Some(Map(site1 -> 2)),
          siteSeenTime = Some(Map(site1 -> timestampMid)),
          pageViews = 2,
          firstSeen = timestampMax,
          lastSeen = timestampMax
        ),
        Snapshot(
          dmpId = dmpId1,
          countries = Map(),
          countrySeenTime = Map(),
          cities = Some(Map()),
          citySeenTime = Some(Map()),
          genders = Some(Map()),
          yobs = Some(Map()),
          keywords = Map(),
          keywordSeenTime = Map(),
          siteIds = Some(Map()),
          siteSeenTime = Some(Map()),
          pageViews = 1,
          firstSeen = timestampMax,
          lastSeen = timestampMin
        ),
        Snapshot(
          dmpId = dmpId1,
          countries = Map(country1 -> 2, country2 -> 1),
          countrySeenTime = Map(country1 -> timestampMin, country2 -> timestampMid),
          cities = Some(Map(country1city2 -> 1)),
          citySeenTime = Some(Map(country1city2 -> timestampMin)),
          genders = Some(Map(gender1 -> 1, gender2 -> 1)),
          yobs = Some(Map(yob1 -> 2, yob2 -> 1)),
          keywords = Map(keyword2 -> 3, keyword3 -> 1),
          keywordSeenTime = Map(keyword2 -> timestampMin, keyword3 -> timestampMax),
          siteIds = Some(Map(site1 -> 1, site2 -> 1)),
          siteSeenTime = Some(Map(site1 -> timestampMax, site2 -> timestampMax)),
          pageViews = 3,
          firstSeen = timestampMin,
          lastSeen = timestampMin
        ),
        Snapshot(
          dmpId = dmpId1,
          countries = Map(),
          countrySeenTime = Map(),
          cities = None,
          citySeenTime = None,
          genders = None,
          yobs = None,
          keywords = Map(),
          keywordSeenTime = Map(),
          siteIds = None,
          siteSeenTime = None,
          pageViews = 1,
          firstSeen = timestampMax,
          lastSeen = timestampMin
        ),
        snapshot1,
        snapshot2
      )

      val initialSnapshotsRDD = sc.parallelize(initialSnapshots)
      val initialSnapshotsShuffledRDD = sc.parallelize(random.shuffle(initialSnapshots))

      val expectedSnapshots = Seq(
        Snapshot(
          dmpId = dmpId1,
          countries = Map(country1 -> 3, country2 -> 1),
          countrySeenTime = Map(country1 -> timestampMax, country2 -> timestampMid),
          cities = Some(Map(country1city1 -> 2, country1city2 -> 1)),
          citySeenTime = Some(Map(country1city1 -> timestampMid , country1city2 -> timestampMin)),
          genders = Some(Map(gender1 -> 3, gender2 -> 1)),
          yobs = Some(Map(yob1 -> 4, yob2 -> 1)),
          keywords = Map(keyword1 -> 2, keyword2 -> 6, keyword3 -> 2),
          keywordSeenTime = Map(keyword1 -> timestampMid, keyword2 -> timestampMax, keyword3 -> timestampMax),
          siteIds = Some(Map(site1 -> 3, site2 -> 1)),
          siteSeenTime = Some(Map(site1 -> timestampMax, site2 -> timestampMax)),
          pageViews = 7,
          firstSeen = timestampMin,
          lastSeen = timestampMax
        ),
        snapshot1,
        snapshot2
      )

      merger.merge(initialSnapshotsRDD).collect() should contain theSameElementsAs expectedSnapshots
      merger.merge(initialSnapshotsShuffledRDD).collect() should contain theSameElementsAs expectedSnapshots
    }
  }

  private def merger = {
    new SnapshotMerger
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
