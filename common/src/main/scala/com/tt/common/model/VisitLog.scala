package com.tt.common.model

import java.util.concurrent.TimeUnit

case class VisitLog(
                     dmpId: String,
                     country: String,
                     city: Option[String] = None,
                     gender: Option[Int] = None,
                     yob: Option[Int] = None,
                     keywords: Option[List[Int]] = None,
                     siteId: Option[Int] = None,
                     utcDays: Int = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis()).toInt
                   )

