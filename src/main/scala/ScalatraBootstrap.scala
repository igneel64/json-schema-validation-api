import com.github.igneel64.JSONSchemaValidator._
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    context.mount(new JSONSchemaServlet, "/schema/*")
    context.mount(new JSONValidatorServlet, "/validate/*")
  }
}
