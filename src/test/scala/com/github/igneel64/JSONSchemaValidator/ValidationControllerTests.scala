package com.github.igneel64.JSONSchemaValidator

import org.scalatra.test.scalatest._

class ValidationControllerTests extends ScalatraFunSuite {

  addServlet(classOf[ValidationController], "/*")

  test("GET / on ValidationController will return 405 as method is not implemented") {
    get("/notfound") {
      assert(status.equals(405))
    }
  }

}
