package com.tt.analyzer.db

import slick.jdbc.JdbcBackend.Database
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

class StatRepository(val profile: JdbcProfile, val db: Database, val schema: Schema) {
  import profile.api._
  import schema._


  protected def usersVisitedSiteCountQuery(siteId: Rep[Int], countryName: Rep[String], genderId: Rep[Int]): Rep[Int] = {
    snapshotStatTQ
      .join(countryStatTQ.join(countryTQ).on(_.countryId === _.id))
      .on{
        case (snapshotStat, (countryStat, _)) => snapshotStat.dmpId === countryStat.dmpId
      }
      .join(genderStatTQ)
      .on{
        case ((snapshotStat, _), genderStat) => snapshotStat.dmpId === genderStat.dmpId
      }
      .join(siteStatTQ)
      .on{
        case (((snapshotStat, _), _), siteStat) => snapshotStat.dmpId === siteStat.dmpId
      }
      .filter {
        case (((_, (_, country)), gender), site) =>
          country.name === countryName && gender.genderId === genderId && site.siteId === siteId
      }
      .length
  }

  protected val usersVisitedSiteCountQueryCompiled = Compiled(usersVisitedSiteCountQuery _)

  protected def mostVisitedSitesQuery(count: Int)/*: Rep[Seq[(Int, Int)]]*/ = {
    siteStatTQ
      .groupBy {
        s => (s.siteId)
      }
      .map {
        case (siteId, group) => (siteId, group.map {_.seenCount}.sum.getOrElse(0) )
      }
      .sortBy{
        case (_, seenCount) => seenCount.desc
      }
      .take(count)
      .result
  }

  protected def mostlyUsedKeywordsQuery(count: Int)/*: Rep[Seq[(Int, Int)]]*/ = {
    keywordStatTQ
      .groupBy {
        s => (s.keywordId)
      }
      .map {
        case (keywordId, group) => (keywordId, group.map {_.seenCount}.sum.getOrElse(0) )
      }
      .sortBy{
        case (_, seenCount) => seenCount.desc
      }
      .take(count)
      .result
  }

  protected def usersWhoDidNotVisitSiteCountQuery(siteId: Rep[Int], fromTimestamp: Rep[Int])/*: Rep[Seq[String]]*/ = {
    dmpTQ
      .filter {
        d => !(d.dmpId in siteStatTQ.filter(
          s => s.siteId === siteId && s.lastSeen > fromTimestamp
        ).map(_.dmpId))
      }
      .length
  }

  protected val usersWhoDidNotVisitSiteCountQueryCompiled = Compiled(usersWhoDidNotVisitSiteCountQuery _)


  def usersVisitedSiteCount(siteId: Int, countryName: String, genderId: Int): Future[Int] =
    db.run(usersVisitedSiteCountQueryCompiled(siteId, countryName, genderId).result)

  def mostVisitedSites(count: Int): Future[Seq[(Int, Int)]] =
    db.run(mostVisitedSitesQuery(count))

  def mostlyUsedKeywords(count: Int): Future[Seq[(Int, Int)]] =
    db.run(mostlyUsedKeywordsQuery(count))

  def usersWhoDidNotVisitSiteCount(siteId: Int, fromTimestamp: Int): Future[Int] =
    db.run(usersWhoDidNotVisitSiteCountQueryCompiled(siteId, fromTimestamp).result)

}
