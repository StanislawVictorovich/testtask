package com.tt.storage

import com.tt.common.model.Snapshot
import com.tt.storage.db._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel

class SnapshotSaver(implicit storage: Storage) {

  protected def zipRDDWithId[T](rdd: RDD[T]): RDD[(T, Long)] = rdd.zipWithUniqueId()

  def store(snapshots: RDD[Snapshot])(implicit sparkSession: SparkSession): Unit = {
    import SnapshotSaver._
    import Storage.implicits._
    import sparkSession.sqlContext.implicits._

    snapshots.persist(StorageLevel.MEMORY_ONLY_SER)

    val dmps = zipRDDWithId(snapshots.map(_.dmpId))

    val countries = zipRDDWithId(snapshots.flatMap(_.countries.keySet).distinct())

    val cities = zipRDDWithId(snapshots.flatMap(_.cities.getOrElse(Map.empty).keySet).distinct())

    dmps
      .map { case (dmp, id) => DmpIdentifiable(dmp, id) }
      .save(DmpIdentifiable)

    countries
      .map { case (country, id) => CountryIdentifiable(id, country) }
      .save(CountryIdentifiable)

    cities
      .map { case (city, id) => CityIdentifiable(id, city) }
      .save(CityIdentifiable)


    val dmpIdStrSnapshots = snapshots.map{ s => (s.dmpId, s) }

    snapshots.unpersist()

    val dmpIdSnapshots = dmps.join(dmpIdStrSnapshots).map {
      case (_, (dmpId, snapshot)) => (dmpId, snapshot)
    }

    dmpIdSnapshots.persist(StorageLevel.MEMORY_ONLY_SER)

    dmpIdSnapshots
      .map{
        case (dmpId, s) => SnapshotStat(dmpId, s.pageViews, s.firstSeen, s.lastSeen)
      }
      .save(SnapshotStat)

    dmpIdSnapshots
      .flatMap {
        case (dmpId, s) => intersectMaps(s.countries, s.countrySeenTime).map {
          case (country, (seenCount, lastSeen)) => (country, (dmpId, seenCount, lastSeen))
        }
      }
      .join(countries)
      .map {
        case (_, ((dmpId, seenCount, lastSeen), countryId)) => CountryStat(dmpId, countryId, seenCount, lastSeen)
      }
      .save(CountryStat)

    dmpIdSnapshots
      .flatMap {
        case (dmpId, s) => intersectOptMaps(s.cities, s.citySeenTime).map {
          case (country, (seenCount, lastSeen)) => (country, (dmpId, seenCount, lastSeen))
        }
      }
      .join(cities)
      .map {
        case (_, ((dmpId, seenCount, lastSeen), cityId)) => CityStat(dmpId, cityId, seenCount, lastSeen)
      }
      .save(CityStat)

    dmpIdSnapshots
      .flatMap {
        case (dmpId, s) => s.genders.getOrElse(Map()).map {
          case (genderId, seenCount) => GenderStat(dmpId, genderId, seenCount)
        }
      }
      .save(GenderStat)

    dmpIdSnapshots
      .flatMap {
        case (dmpId, s) => s.yobs.getOrElse(Map()).map {
          case (yob, seenCount) => YobStat(dmpId, yob, seenCount)
        }
      }
      .save(YobStat)

    dmpIdSnapshots
      .flatMap {
        case (dmpId, s) => intersectMaps(s.keywords, s.keywordSeenTime).map {
          case (keywordId, (seenCount, lastSeen)) => KeywordStat(dmpId, keywordId, seenCount, lastSeen)
        }
      }
      .save(KeywordStat)

    dmpIdSnapshots
      .flatMap {
        case (dmpId, s) => intersectOptMaps(s.siteIds, s.siteSeenTime).map {
          case (siteId, (seenCount, lastSeen)) => SiteStat(dmpId, siteId, seenCount, lastSeen)
        }
      }
      .save(SiteStat)
  }
}

object SnapshotSaver {
  private def intersectOptMaps[T](a: Option[Map[T, Int]], b: Option[Map[T, Int]]) = {
    val am = a.getOrElse(Map())
    val bm = b.getOrElse(Map())
    (am.keySet ++ bm.keySet).map(k => k -> (am.getOrElse(k, 0), bm.getOrElse(k, 0))).toMap
  }

  private def intersectMaps[T](am: Map[T, Int], bm: Map[T, Int]) = {
    (am.keySet ++ bm.keySet).map(k => k -> (am.getOrElse(k, 0), bm.getOrElse(k, 0))).toMap
  }
}