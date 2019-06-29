package com.github.igneel64.JSONSchemaValidator
import org.scalatra.ActionResult
import scala.concurrent.Future

/** 
 * Trait modeling the database Operations for any type of client.
 */
trait Operations{
  def initDatabase: Any

  def insertOrUpdateSchema(schemaId: String, jsonToInsertOrUpdate: String): Future[ActionResult]

  def getSchemaIfExists(schemaId: String): Future[ActionResult]
  
  def getSchemaSync(schemaId: String): Any

  def closeDatabaseConnection: Unit
}
