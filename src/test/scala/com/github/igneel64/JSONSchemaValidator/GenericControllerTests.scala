package com.github.igneel64.JSONSchemaValidator

import org.scalatra.test.scalatest._

class GenericControllerTests extends ScalatraFunSuite {

  addServlet(classOf[GenericController], "/*")

  test("GET / on GenericController should return a 404 response") {
    get("/notfound") {
      assert(status.equals(404))
    }
  }

}
