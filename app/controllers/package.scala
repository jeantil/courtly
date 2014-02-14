package object controllers {

  import play.api.data.validation.ValidationError
  import play.api.libs.json._
  import play.api.mvc.Results._
  import play.api.Logger

  def jsonErrors2BadRequest(errors: Seq[(JsPath, Seq[ValidationError])], logger: Logger)(implicit app: play.api.Application) = {
    logger.debug(Json.prettyPrint(JsError.toFlatJson(errors)))
    if (app.mode != play.api.Mode.Prod) {
      BadRequest(JsError.toFlatJson(errors))
    }
    else BadRequest
  }
}
