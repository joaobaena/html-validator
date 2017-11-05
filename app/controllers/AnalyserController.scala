package controllers

import javax.inject._

import models.{AnalysisResponse, Valid}
import java.net.{URL, UnknownHostException}

import play.api.libs.ws._

import scala.util.{Failure, Success, Try}
import javax.inject.Inject

import scala.concurrent.Future
import org.jsoup.Jsoup
import org.jsoup.nodes.{Document}
import play.api.mvc._

import scala.concurrent.ExecutionContext
import play.api.libs.json._

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class AnalyserController @Inject()(cc: ControllerComponents)(ws: WSClient)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with play.api.i18n.I18nSupport {
  implicit val validWrites = Json.writes[Valid]
  implicit val validReads = Json.reads[Valid]
  implicit val analysisResponseWrites = Json.writes[AnalysisResponse]
  implicit val analysisResponseReads = Json.reads[AnalysisResponse]

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def submitUrl(url: String) = Action.async { implicit request =>
    Try(new URL(url)) match {
      case Failure(_) => {
        Future(BadRequest(s"$url is not a valid url"))
      }
      case Success(_) => {
        val req: WSRequest = ws.url(url)
        req.withFollowRedirects(true).get().flatMap { r =>
          r.status match {
            case 200  => {
              val doc: Document = Jsoup.parse(r.body)
              val (externalLinks, internalLinks) = AnalysisResponse.partitionLinks(doc, url)
              val validLinks: List[String] = externalLinks ++ internalLinks

              val listFutures = validLinks.map{
                link => ws.url(link).withFollowRedirects(true).get()
              }.map(_.map {
                r => Valid(r.status.toString, r.statusText)
              }).map{
                f => f.recover {
                  case ex => Valid("Failed Request", ex.toString)
                }}

              val results = Future.sequence(listFutures)
              results.map { status =>
                val s: List[Valid] = status
                val r = AnalysisResponse(
                  AnalysisResponse.fetchVersion(doc),
                  AnalysisResponse.fetchTitle(doc),
                  internalLinks.size,
                  externalLinks.size,
                  validLinks.zip(s).toMap)

                Ok(Json.toJson(r))
              }
            }
            case _ => Future(BadRequest(s"$url unreacheable"))
          }
        }.recover {
          case ex: UnknownHostException => BadRequest(s"$url unreacheable")
          case _ => InternalServerError("Unknown Error")}
      }
    }
  }
}