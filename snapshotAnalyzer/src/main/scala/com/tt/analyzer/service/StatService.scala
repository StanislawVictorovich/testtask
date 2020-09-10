package com.tt.analyzer.service

import scala.concurrent.Future

trait StatService {
  def usersVisitedSiteCount(siteId: Int, country: String, gender: Int): Future[Int]

  def mostVisitedSites(count: Int): Future[Seq[(Int, Int)]]

  def mostlyUsedKeywords(count: Int): Future[Seq[(Int, Int)]]

  def usersWhoDidNotVisitSiteCount(siteId: Int, fromTimestamp: Int): Future[Int]
}
