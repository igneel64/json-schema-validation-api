package com.github.igneel64
import scala.collection.mutable.ArrayBuffer
import net.liftweb.json.{compactRender, parse, JField, JNull, JValue}

import com.github.fge.jsonschema.main.JsonSchemaFactory
import com.github.fge.jsonschema.core.report.{ProcessingMessage, ProcessingReport}

import org.json4s._
import org.json4s.jackson.JsonMethods._

import com.fasterxml.jackson.databind.JsonNode

/*
 * Package object containing shared functionality
 */
package object JSONSchemaValidator {
  case class SchemaResponse(
    action: String,
    id: String,
    status: String,
    message: Option[String] = None
  )

  case class ValidationResponse(
    action: String,
    id: String,
    status: String,
    message: Option[ArrayBuffer[String]] = None
  )

  /** Add schemaId attribute for query purposes
   *
   *  @param schema existing JSON schema
   *  @param schemaId schemaId value to add
   */
  private def addSchemaId(schema: JValue, schemaId: String): JValue = {
    schema.merge(parse(s""" {"schemaId" : "${schemaId}"} """))
  }

  /** Operates and "cleans" the JSON input that the client has provided
   *
   *  @param parsedJson JSON input
   *  @param schemaId identifier of the current schema
   */
  def cleanParsedJson(parsedJson: JValue, schemaId: String): String = {
    
    // Remove the null values from JSON provided and 
    // remove "$schema" key as it is reserved in MongodDB without aliasing capability
    val cleanValue = parsedJson
      .removeField {
        case JField("$schema", _) => true
        case _                    => false
      }
      .remove(_ == JNull)

    val identifiedSchema = addSchemaId(cleanValue, schemaId)
    compactRender(
      identifiedSchema
    )
  }

  def validateJSON(schema: String, jsonToValidate: String): ProcessingReport = {
    val schemaNode: JsonNode =
      asJsonNode(org.json4s.jackson.JsonMethods.parse(schema))
    val instance: JsonNode = asJsonNode(org.json4s.jackson.JsonMethods.parse(jsonToValidate))

    val validator = JsonSchemaFactory.byDefault().getValidator
    validator.validate(schemaNode, instance)
  }
}
