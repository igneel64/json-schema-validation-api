# Scala JSON Schema Validator

## App
Deployed on Heroku [live](https://still-brook-39140.herokuapp.com).
Developed mainly with 
- Scalatra
- MongoDB
## Development requirements ##

- Scala Version [2.12](https://www.scala-lang.org/download/2.12.8.html)
- [sbt](https://www.scala-sbt.org/download.html) (Developed with version 1.2.1)
- [MongoDB 3.4](https://www.mongodb.com/download-center/community) (Probably will work also with 3.6)

## Basic Endpoints

- [POST](https://github.com/igneel64/json-schema-validation-api/blob/69c80684e94c074ea1bf368652a063139014fa18/src/main/scala/com/github/igneel64/JSONSchemaValidator/SchemaController.scala#L26)    /schema/:schemaId        - Upload a JSON Schema with unique schemaId
- [GET](https://github.com/igneel64/json-schema-validation-api/blob/69c80684e94c074ea1bf368652a063139014fa18/src/main/scala/com/github/igneel64/JSONSchemaValidator/SchemaController.scala#L59)     /schema/:schemaId        - Download a JSON Schema with unique schemaId

- [POST](https://github.com/igneel64/json-schema-validation-api/blob/69c80684e94c074ea1bf368652a063139014fa18/src/main/scala/com/github/igneel64/JSONSchemaValidator/ValidationController.scala#L35)    /validate/:schemaId      - Validate a JSON document against the JSON Schema identified by schemaId


## Local setup
1. Clone the repo
2. Have a `mongod` instance running
3. Then run:
```sh
$ cd json-schema-validator
$ sbt
> jetty:start
```
Open [http://localhost:8080/](http://localhost:8080/) in your browser.

### Code reloading
To enable automatic code reloading enter:
```sh
> ~;jetty:stop;jetty:start
```

### Linting & formatting
Currently the repo is using [Scalafmt](https://scalameta.org/scalafmt/) with standard rules and [Scalastyle](http://www.scalastyle.org/) for basic linting.
