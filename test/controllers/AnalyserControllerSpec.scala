package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._

class AnalyserControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "AnalyserController index GET" should {

    "render the index page from the application" in {
      val controller = inject[AnalyserController]
      val home = controller.index().apply(FakeRequest(GET, "/"))

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include ("Enter URL to be analysed")
    }
  }
  "AnalyserController submitUrl POST" should {

    "respond with BadRequest for strings that are not URL" in {
      val controller = inject[AnalyserController]
      val home = controller.submitUrl("ajkskaj").apply(FakeRequest(POST, "/"))

      status(home) mustBe 400
      contentType(home) mustBe Some("text/plain")
      contentAsString(home) must include ("is not a valid url")
    }

    "respond with BadRequest for unreachable URLs" in {
      val controller = inject[AnalyserController]
      val home = controller.submitUrl("http://www.ig.com.bb").apply(FakeRequest(POST, "/"))

      status(home) mustBe 400
      contentType(home) mustBe Some("text/plain")
      contentAsString(home) must include ("unreacheable")
    }

    "respond with Ok for reachable URLs" in {
      val controller = inject[AnalyserController]
      val home = controller.submitUrl("http://www.google.com").apply(FakeRequest(POST, "/"))

      status(home) mustBe 200
      contentType(home) mustBe Some("application/json")
      //contentAsJson(home) must include ("OK")
    }
  }

}
