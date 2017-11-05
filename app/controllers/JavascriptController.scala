package controllers
import javax.inject.Inject

import play.api.mvc._
import play.api.routing._

class JavascriptController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def jsRoutes = Action { implicit request =>
    Ok(
      JavaScriptReverseRouter("jsRoutes")(
        routes.javascript.AnalyserController.submitUrl
      )
    ).as("text/javascript")
  }

}
