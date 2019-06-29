package com.github.igneel64.JSONSchemaValidator

import org.scalatra.test.scalatest._

class JSONSchemaServletTests extends ScalatraFunSuite {

  addServlet(classOf[JSONSchemaServlet], "/*")

  test("GET / on JSONSchemaServlet should return status 200") {
    get("/") {
      status should equal (200)
    }
  }

}
