import com.github.igneel64.JSONSchemaValidator._
import org.scalatra._
import javax.servlet.ServletContext
import org.mongodb.scala.bson.collection.mutable.Document
import com.mongodb.MongoSocketException

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    MongoDBClient.initDatabase()

    context.mount(new SchemaController, "/schema/*")
    context.mount(new ValidationController, "/validate/*")
    context.mount(new GenericController, "/*")

    val environment = sys.env.getOrElse("SCALATRA_ENV", "development")
    context.initParameters("org.scalatra.environment") = environment
    println(s"Starting at ${environment}")
  }

  override def destroy(context: ServletContext) {
    MongoDBClient.closeDatabaseConnection()
  }
}
