package com.github.igneel64.JSONSchemaValidator

import org.scalatra._
import net.liftweb.json.{parse, DefaultFormats, Serialization}

import scala.util.control.Exception._

import concurrent._
import ExecutionContext.global

class SchemaController extends ScalatraServlet with FutureSupport {

  implicit val formats = DefaultFormats
  implicit protected def executor: ExecutionContext = global

  /*
   * Set content type explicitly since all the responses are by contract JSON
   */
  before() {
    response.addHeader("Content-Type", "application/json")
  }

  /*
   * Insert new schema with schemaId. Update if it already exists
   */
  post("/:schemaId/?") {
    val schemaId = params("schemaId")
    val jsonBody = request.body
    val matcher = allCatch.either(parse(jsonBody)) match {
      case Left(value) => {
        Ok(
          Serialization.write(
            SchemaResponse(
              "uploadSchema",
              schemaId,
              "error",
              Some("Invalid JSON")
            )
          )
        )
      }
      case Right(value) => {
        val cleanJson = cleanParsedJson(value, schemaId);
        val result = MongoDBClient.insertOrUpdateSchema(schemaId, cleanJson);
        new AsyncResult {
          val is =
            result
        }
      }
    }

    matcher
  }


  /*
   * Get schema with schemaId.
   */
  get("/:schemaId/?") {
    val schemaId = params("schemaId");
    val result = MongoDBClient.getSchemaIfExists(schemaId)

    new AsyncResult {
      val is =
        result
    }
  }

  notFound {
    NotFound("""{ "error": "The location could not be found" }""")
  }

  error {
    case e: Throwable => {
      InternalServerError("""{ "error": "Application error" }""")
    }
  }

}
