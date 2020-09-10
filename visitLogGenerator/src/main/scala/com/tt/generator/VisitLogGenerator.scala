package com.tt.generator

import com.tt.common.model.VisitLog
import com.tt.common.util.Constants._

import scala.util.Random

class VisitLogGenerator(seed: Option[Long] = None) {
  private val random = new Random

  private val countryCities = Map(
    "Russia" -> Seq(
      Some("Moscow"),
      Some("Saint Petersburg")
    ),
    "Usa" -> Seq(
      Some("Washington"),
      Some("New York"),
      None
    ),
    "Germany" -> Seq(
      Some("Berlin"),
      Some("Hamburg")
    ),
    "Lithuania" -> Seq(
      Some("Vilnius"),
      Some("Kaunas"),
      None
    ),
    "China" -> Seq(
      Some("Beijing"),
      Some("Wuhan"),
      None
    )
  )

  private val genders = Seq(
    Some(GENDER_MALE), Some(GENDER_FEMALE), None
  )
  private val yearsOfBirth = (1950 to 2020).map(Some(_)) ++ Seq(None)

  private val keywordCollections = (1 to 50).map(
    _ => random.shuffle((1 to 100).toList).take(random.nextInt(10))
  ).map(Some(_)) ++ Seq(None)

  private val siteIds =  (1 to 50).map(Some(_)) ++ Seq(None)

  def generate(dmpId: Int): VisitLog = {
    val country = countryCities.keys.toSeq(random.nextInt(countryCities.size))
    val cities = countryCities.getOrElse(country, throw new Exception("country somehow not found"))

    VisitLog(
      dmpId.toString,
      country,
      cities(random.nextInt(cities.size)),
      genders(random.nextInt(genders.size)),
      yearsOfBirth(random.nextInt(yearsOfBirth.size)),
      keywordCollections(random.nextInt(keywordCollections.size)),
      siteIds(random.nextInt(siteIds.size)),
      random.nextInt.abs
    )
  }
}

object VisitLogGenerator {
  def apply() = new VisitLogGenerator()
}
