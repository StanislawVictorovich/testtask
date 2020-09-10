package com.tt.storage.util

import java.util.Properties

class JdbcConfig(val url: String, userName: String, password: String, driver: String) {
  val props = new Properties()
  props.put("user", userName)
  props.put("password", password)
  props.put("driver", driver)
}
