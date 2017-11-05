package models

import org.jsoup.Jsoup
import org.scalatestplus.play.PlaySpec

class AnalysisResponseSpec extends PlaySpec {
  "AnalysisResponse from a html" should {
    val testHtml1 = """<!DOCTYPE html>
                     |<html>
                     |<head>
                     |  <title>Title 1</title>
                     |</head>
                     |<body>
                     |
                     |</body>
                     |</html>""".stripMargin
    val testHtml2 = """<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 3.2 FINAL//EN">
                      |<html>
                      |<head>
                      |  <title>Title 2</title>
                      |</head>
                      |<body>
                      |<a href="http://www.foo.com/link1/">Link1</a>
                      |<a href="http://www.foo.com/link1/">Link1 again</a>
                      |<a href="https://www.foo.com/link2/">Link2</a>
                      |<a href="https://www.anotherfoo.com/">Link3</a>
                      |<a href="internal.asp">Internal Link</a>
                      |</body>
                      |</html>""".stripMargin

    val url = "http://www.foo.com"
    val doc1 = Jsoup.parse(testHtml1)
    val doc2 = Jsoup.parse(testHtml2)


    "fetch the HTML version of the page" in {
      val versionFive = AnalysisResponse.fetchVersion(doc1)
      val versionOld = AnalysisResponse.fetchVersion(doc2)
      versionFive must include ("HTML 5")
      versionOld must include ("HTML 3.2")
    }

    "fetch the page's title" in {
      val titleOne = AnalysisResponse.fetchTitle(doc1)
      val titleTwo = AnalysisResponse.fetchTitle(doc2)
      titleOne must include ("Title 1")
      titleTwo must include ("Title 2")
    }

    "get a count of the amount of unique links in the page" in {
      val (external, internal) = AnalysisResponse.partitionLinks(doc2, url)
      (internal ++ external).size mustBe 4
      internal.size mustBe 3
      external.size mustBe 1
    }

  }
}
