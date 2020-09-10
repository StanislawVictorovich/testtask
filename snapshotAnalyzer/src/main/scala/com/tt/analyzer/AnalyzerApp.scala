package com.tt.analyzer

import com.tt.analyzer.di.StatServiceAware
import com.tt.common.util.Constants._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object AnalyzerApp extends StatServiceAware {
  def main(args: Array[String]): Unit = {

    val country = "Lithuania"
    val siteId = 30

    val usersVisitedSiteCount =  await(statService.usersVisitedSiteCount(siteId, country, GENDER_MALE))

    println(s"$usersVisitedSiteCount users from $country with male gender visited site $siteId")


    val mostVisitedSites =  await(statService.mostVisitedSites(10)).map { case (k,v) => k }.mkString(", ")

    println(s"$mostVisitedSites are 10 most visited sites")


    val mostlyUsedKeywords =  await(statService.mostlyUsedKeywords(10)).map { case (k,v) => k }.mkString(", ")

    println(s"$mostlyUsedKeywords are 10 most used keywords")


    val currentMoment = 100000
    val daysCount = 14
    val fromTimestamp = currentMoment - (60 * 60 * 24 * daysCount)
    val siteIdForChecking = 13

    val usersWhoDidNotVisitSiteCount =  await(statService.usersWhoDidNotVisitSiteCount(siteIdForChecking, fromTimestamp))

    println(s"$usersWhoDidNotVisitSiteCount people were not visited site $siteIdForChecking for $daysCount days")
  }

  def await[T](f: Future[T]): T = {
    Await.result(f, 60.seconds)
  }
}
