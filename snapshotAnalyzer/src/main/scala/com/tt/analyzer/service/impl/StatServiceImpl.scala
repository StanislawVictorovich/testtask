package com.tt.analyzer.service.impl

import com.tt.analyzer.db.StatRepository
import com.tt.analyzer.service.StatService

import scala.concurrent.Future

class StatServiceImpl(statRepository: StatRepository) extends StatService {
  override def usersVisitedSiteCount(siteId: Int, country: String, gender: Int): Future[Int] =
    statRepository.usersVisitedSiteCount(siteId, country, gender)

  override def mostVisitedSites(count: Int): Future[Seq[(Int, Int)]] =
    statRepository.mostVisitedSites(count)

  override def mostlyUsedKeywords(count: Int): Future[Seq[(Int, Int)]] =
    statRepository.mostlyUsedKeywords(count)

  override def usersWhoDidNotVisitSiteCount(siteId: Int, fromTimestamp: Int): Future[Int] =
    statRepository.usersWhoDidNotVisitSiteCount(siteId, fromTimestamp)
}
