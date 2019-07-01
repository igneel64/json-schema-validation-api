package com.github.igneel64.JSONSchemaValidator

import org.scalatra._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import concurrent._
import ExecutionContext.global

import scala.util.control.Exception._
import scala.collection.mutable.ArrayBuffer

import com.github.fge.jsonschema.core.report.ProcessingMessage
import org.json4s._
import org.json4s.jackson.JsonMethods._

import net.liftweb.json.{DefaultFormats, Serialization, parse => liftParse}

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

class ValidationController extends ScalatraServlet {

  implicit val formats = DefaultFormats
  implicit protected def executor: ExecutionContext = global

  before() {
    response.addHeader("Content-Type", "application/json")
  }

  /*
   * Validate given JSON document with stored JSON schema with schemaId.
   */
  post("/:schemaId/?") {
    val schemaId = params("schemaId");
    val jsonBody = request.body

    val matcher = allCatch.either(liftParse(jsonBody)) match {
      case Left(value) => {
        Left(
          Ok(
            Serialization.write(
              SchemaResponse(
                "validateSchema",
                schemaId,
                "error",
                Some("Invalid JSON")
              )
            )
          )
        )
      }
      case Right(value) => {
        val cleanJson = cleanParsedJson(value, schemaId);
        Right(cleanJson)
      }
    }

    val result = matcher.right.map(parsedJson => {
      val schema = MongoDBClient.getSchemaSync(schemaId);

      if (schema.isEmpty) {
        ValidationResponse(
          "validateDocument",
          schemaId,
          "error",
          Option(ArrayBuffer("Resource not found"))
        )
      } else {
        val processingReport = validateJSON(schema.head.toJson(), parsedJson)
        val jsonResponse = if (processingReport.isSuccess) {
          ValidationResponse("validateDocument", schemaId, "success")
        } else {
          var messageArray = ArrayBuffer[String]();
          processingReport.forEach { message: ProcessingMessage =>
            {
              var messagePointer = message.asJson().at("/schema/pointer").asText()
              var messageContent = message.getMessage()
              var errorMessage = if (messagePointer.isEmpty()) {
                messageContent
              } else {
                messagePointer + " : " + messageContent
              }
              messageArray += errorMessage
            }
          }
          ValidationResponse("validateDocument", schemaId, "error", Option(messageArray))
        }
        jsonResponse
      }
    })

    val mapper = new ObjectMapper() with ScalaObjectMapper
    mapper.registerModule(DefaultScalaModule)
    Ok(mapper.writeValueAsString(result.merge))
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
