package com.tt.storage

import com.holdenkarau.spark.testing.{RDDComparisons, SharedSparkContext}
import com.tt.common.model.Snapshot
import com.tt.storage.db._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Encoder, SparkSession}
import org.scalamock.scalatest.MockFactory

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class SnapshotSaverTest extends AnyWordSpec with SharedSparkContext with RDDComparisons with MockFactory {

  "store" should {
    "store data" in {

      val dmpId1 = "dmpId1"
      val dmpId1Id = 0L
      val dmpId2 = "dmpId2"
      val dmpId2Id = 1L
      val dmpId3 = "dmpId3"
      val dmpId3Id = 2L
      val country1 = "country1"
      val country1Id = 0L
      val country2 = "country2"
      val country2Id = 1L
      val gender1 = 100
      val gender2 = 101
      val country1city1 = "country1city1"
      val country1city1Id = 0L
      val country1city2 = "country1city2"
      val country1city2Id = 1L
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

      val snapshots = sc.parallelize(Seq(
        Snapshot(
          dmpId = dmpId1,
          countries = Map(country1 -> 4, country2 -> 1),
          countrySeenTime = Map(country1 -> timestampMax, country2 -> timestampMid),
          cities = Some(Map(country1city1 -> 2, country1city2 -> 1)),
          citySeenTime = Some(Map(country1city1 -> timestampMid , country1city2 -> timestampMax)),
          genders = Some(Map(gender1 -> 20, gender2 -> 10)),
          yobs = Some(Map(yob1 -> 2000, yob2 -> 1000)),
          keywords = Map(keyword1 -> 20000, keyword2 -> 30000, keyword3 -> 10000),
          keywordSeenTime = Map(keyword1 -> timestampMid, keyword2 -> timestampMax, keyword3 -> timestampMax),
          siteIds = Some(Map(site1 -> 200000, site2 -> 100000)),
          siteSeenTime = Some(Map(site1 -> timestampMid, site2 -> timestampMax)),
          pageViews = 5,
          firstSeen = timestampMin,
          lastSeen = timestampMax
        ),
        Snapshot(
          dmpId = dmpId2,
          countries = Map(country1 -> 2),
          countrySeenTime = Map(country1 -> timestampMax),
          cities = Some(Map(country1city2 -> 3)),
          citySeenTime = Some(Map(country1city2 -> timestampMax)),
          genders = Some(Map(gender2 -> 30)),
          yobs = Some(Map(yob2 -> 3000)),
          keywords = Map(),
          keywordSeenTime = Map(),
          siteIds = Some(Map(site2 -> 300000)),
          siteSeenTime = Some(Map(site2 -> timestampMin)),
          pageViews = 1,
          firstSeen = timestampMax,
          lastSeen = timestampMax
        ),
        Snapshot(
          dmpId = dmpId3,
          countries = Map(country2 -> 3),
          countrySeenTime = Map(country2 -> timestampMin),
          cities = None,
          citySeenTime = None,
          genders = None,
          yobs = None,
          keywords = Map(keyword2 -> 40000, keyword3 -> 50000),
          keywordSeenTime = Map(keyword2 -> timestampMax, keyword3 -> timestampMin),
          siteIds = None,
          siteSeenTime = None,
          pageViews = 2,
          firstSeen = timestampMin,
          lastSeen = timestampMin
        )
      ))


      val dmps = Seq(
        DmpIdentifiable(dmpId1, dmpId1Id),
        DmpIdentifiable(dmpId2, dmpId2Id),
        DmpIdentifiable(dmpId3, dmpId3Id)
      )
      val countries = Seq(
        CountryIdentifiable(country1Id, country1),
        CountryIdentifiable(country2Id, country2)
      )
      val cities = Seq(
        CityIdentifiable(country1city1Id, country1city1),
        CityIdentifiable(country1city2Id, country1city2)
      )
      val snapshotStat = Seq(
        SnapshotStat(dmpId1Id, 5, timestampMin, timestampMax),
        SnapshotStat(dmpId2Id, 1, timestampMax, timestampMax),
        SnapshotStat(dmpId3Id, 2, timestampMin, timestampMin)
      )
      val countryStat = Seq(
        CountryStat(dmpId1Id, country1Id, 4, timestampMax),
        CountryStat(dmpId1Id, country2Id, 1, timestampMid),
        CountryStat(dmpId2Id, country1Id, 2, timestampMax),
        CountryStat(dmpId3Id, country2Id, 3, timestampMin),
      )
      val cityStat = Seq(
        CityStat(dmpId1Id, country1city1Id, 2, timestampMid),
        CityStat(dmpId1Id, country1city2Id, 1, timestampMax),
        CityStat(dmpId2Id, country1city2Id, 3, timestampMax),
      )
      val genderStat = Seq(
        GenderStat(dmpId1Id, gender1, 20),
        GenderStat(dmpId1Id, gender2, 10),
        GenderStat(dmpId2Id, gender2, 30),
      )
      val yobStat = Seq(
        YobStat(dmpId1Id, yob1, 2000),
        YobStat(dmpId1Id, yob2, 1000),
        YobStat(dmpId2Id, yob2, 3000),
      )
      val keywordStat = Seq(
        KeywordStat(dmpId1Id, keyword1, 20000, timestampMid),
        KeywordStat(dmpId1Id, keyword2, 30000, timestampMax),
        KeywordStat(dmpId1Id, keyword3, 10000, timestampMax),
        KeywordStat(dmpId3Id, keyword2, 40000, timestampMax),
        KeywordStat(dmpId3Id, keyword3, 50000, timestampMin),
      )
      val siteStat = Seq(
        SiteStat(dmpId1Id, site1, 200000, timestampMid),
        SiteStat(dmpId1Id, site2, 100000, timestampMax),
        SiteStat(dmpId2Id, site2, 300000, timestampMin),
      )

      var checksCount = 0
      val expectedNumberOfCount = 10

      implicit val storageMock: Storage = new Storage{
        override def save[T: Encoder](rdd: RDD[T], tableModel: TableModel)
                                     (implicit sparkSession: SparkSession): Unit = {
          val data = rdd.collect()
          checksCount+=1
          tableModel match {
            case DmpIdentifiable => data should contain theSameElementsAs dmps
            case CountryIdentifiable => data should contain theSameElementsAs countries
            case CityIdentifiable => data should contain theSameElementsAs cities
            case SnapshotStat => data should contain theSameElementsAs snapshotStat
            case CountryStat => data should contain theSameElementsAs countryStat
            case CityStat => data should contain theSameElementsAs cityStat
            case GenderStat => data should contain theSameElementsAs genderStat
            case YobStat => data should contain theSameElementsAs yobStat
            case KeywordStat => data should contain theSameElementsAs keywordStat
            case SiteStat => data should contain theSameElementsAs siteStat
            case tm => throw new Exception(s"unsupported data $tm")
          }
        }
      }


      saver.store(snapshots)

      checksCount shouldBe expectedNumberOfCount
    }
  }

  private def saver(implicit storage: Storage) = {
    new SnapshotSaver {
      override protected def zipRDDWithId[T](rdd: RDD[T]): RDD[(T, Long)] = rdd.zipWithIndex()
    }
  }

  implicit private def sparkSession: SparkSession = {
    SparkSession.builder.config(sc.getConf).getOrCreate()
  }
}
