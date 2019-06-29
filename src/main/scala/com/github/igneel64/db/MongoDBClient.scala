package com.github.igneel64.JSONSchemaValidator

import org.scalatra._

import org.mongodb.scala.{
  Completed,
  MongoClient,
  MongoCollection,
  MongoDatabase,
  Observable,
  SingleObservable,
  Observer
}
import org.mongodb.scala.model.Projections.{excludeId, fields}
import org.mongodb.scala.model.Filters._
import com.mongodb.client.model.FindOneAndReplaceOptions
import org.mongodb.scala.bson.collection.mutable.Document

import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.duration.Duration
import concurrent._
import ExecutionContext.global

import scala.util.{Failure, Success, Try}

import net.liftweb.json.Serialization
import net.liftweb.json.DefaultFormats

/** 
 * MongoDB client
 */
object MongoDBClient extends Operations{

  /** 
   * Implicit functions that execute the MongoDB Observable and return the results synchronously
   */
  val waitDuration = Duration(5, "seconds")
  implicit class ObservableExecutor[T](observable: Observable[T]) {
    def execute(): Seq[T] = Await.result(observable.toFuture(), waitDuration)
  }

  implicit class SingleObservableExecutor[T](observable: SingleObservable[T]) {
    def execute(): T = Await.result(observable.toFuture(), waitDuration)
  }
  // end implicit functions for Observable queries

  implicit protected def executor: ExecutionContext = global
  implicit val formats = DefaultFormats

  val defaultMongoURL = "mongodb://localhost"
  val defaultDbName = "scala-json-schema-validator"
  val mongoHost = sys.env.getOrElse("MONGODB_HOST", defaultMongoURL)
  val mongoDbName = sys.env.getOrElse("MONGODB_DATABASE_NAME", defaultDbName)

  val mongoClient: MongoClient = MongoClient(mongoHost)
  val schemasDatabase: MongoDatabase = mongoClient.getDatabase(mongoDbName)
  val schemaCollection: MongoCollection[Document] = schemasDatabase.getCollection("schemas")

  def initDatabase(): MongoDatabase = {
    mongoClient.getDatabase("scala-json-schema-validator")
  }

  def insertOrUpdateSchema(schemaId: String, jsonToInsertOrUpdate: String): Future[ActionResult] = {
    val prom = Promise[ActionResult]()

    val doc: Document = Document(jsonToInsertOrUpdate)
    schemaCollection
      .findOneAndReplace(
        equal("schemaId", schemaId),
        doc,
        new FindOneAndReplaceOptions().upsert(true)
      )
      .toFuture()
      .onComplete {
        case Success(value) => {
          prom.complete(
            Try(
              Ok(
                Serialization
                  .write(SchemaResponse("uploadSchema", schemaId, "success"))
              )
            )
          )
        }
        case Failure(exception) => {
          println(exception)
          prom.complete(Try(BadRequest(""" { "reason": "Something went wrong" } """)))
        }
      }

    prom.future
  }

  def getSchemaIfExists(schemaId: String): Future[ActionResult] = {
    val prom = Promise[ActionResult]()

    schemaCollection
      .find(equal("schemaId", schemaId))
      .projection(fields(excludeId()))
      .first()
      .toFuture()
      .onComplete {
        case Success(res) => {
          if (res == null) {
            prom.complete(
              Try(
                NotFound(
                  """ { "reason": "resource not found" } """
                )
              )
            )
          } else {
            prom.complete(
              Try(Ok(res.toJson()))
            )
          }
        }
        case Failure(exception) => {
          println(exception)
          prom.complete(Try(InternalServerError(exception.getMessage())))
        }
      }
    prom.future
  }

  def getSchemaSync(schemaId: String): Seq[Document] = {
    try {
      schemaCollection.find(equal("schemaId", schemaId)).execute()
    }catch{
      case e:Exception => throw e
    }
  }

  def closeDatabaseConnection() {
    mongoClient.close()
  }
}
