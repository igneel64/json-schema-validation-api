package com.github.igneel64.JSONSchemaValidator

import org.scalatra._

class GenericController extends ScalatraServlet {

  /*
   * Generic root
   */
  get("/") {
    <h1>Welcome to the app</h1>
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
