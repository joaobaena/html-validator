package models

import java.net.URL
import org.jsoup.nodes.{Document, Element, Node}
import scala.collection.JavaConverters._
import scala.util.Try

case class AnalysisResponse(version: String,
                            title: String,
                            internalLinks: Int,
                            externalLinks: Int,
                            linksValidation: Map[String, Valid])
object AnalysisResponse {

  def fetchVersion(doc: Document): String = {
    val nodes = doc.childNodes().asScala.toList
    val regex = raw"(HTML|html)( [0-9](.[0-9]+)?)?".r
    if (nodes.nonEmpty) {
      val pId = nodes.head.attr("publicid")
      val name = nodes.head.attr("name")
      regex.findFirstIn(pId) match {
        case Some(s) => s
        case None => if (name == "html") "HTML 5" else "No version specified"
      }
    }
    else "No version specified"
  }

  def fetchTitle(doc: Document): String = doc.title

  def partitionLinks(doc: Document, url: String): (List[String], List[String]) = {
    val hyperlinks: List[Element] = doc.select("a").asScala.toList
    val links: Set[String] = hyperlinks.map(_.attr("href")).toSet
    val (eLinks, iPaths) = links.partition(l => Try(new URL(l)).isSuccess && !l.contains(url.dropWhile(_!='/')))
    val iLinks = iPaths.map(i => if (i.contains(url.dropWhile(_!='/'))) i else url + i).filter(l => Try(new URL(l)).isSuccess).toList
    (eLinks.toList, iLinks)
  }
}

case class Valid(isReachable: String, msg: String)
