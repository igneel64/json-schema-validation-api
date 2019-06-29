package com.github.igneel64.JSONSchemaValidator

import org.scalatra.test.scalatest._
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

class SchemaControllerTests extends ScalatraFunSuite {

  addServlet(classOf[SchemaController], "/*")

  val mapper = new ObjectMapper() with ScalaObjectMapper
  mapper.registerModule(DefaultScalaModule)

  test("GET / on SchemaController should return a proper 404 response") {
    get("/notfound") {
      val jsonResponse: JsonNode = mapper.readTree(response.body);
      val reason = jsonResponse.get("reason").textValue;
      assert(reason.equals("resource not found"))
      assert(status.equals(404))
    }
  }

}
