package controllers

import com.github.simplyscala.{MongoEmbedDatabase, MongodProps}
import models.Person
import org.joda.time.DateTime
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.collections.bson.BSONCollection
import services.PersonService
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, _}
import scala.concurrent.duration._

@RunWith(classOf[JUnitRunner])
class PersonControllerTest extends PlaySpec with GuiceOneServerPerSuite with MongoEmbedDatabase with BeforeAndAfter with BeforeAndAfterAll {
  var mongoProps: MongodProps = null

  override def beforeAll() = mongoProps = mongoStart()

  override def afterAll() = mongoStop(mongoProps)

  before {
    clearAllCollections
  }

  "PersonController" should {

    "post person" in {
      val Some(result) = route(app, FakeRequest(POST, "/persons?name=ann2&age=6"))

      status(result) mustEqual OK
      contentType(result) mustEqual Some("application/json")

      val json = contentAsJson(result)
      (json \ "age").as[Long] mustBe 6
      (json \ "name").as[String] mustBe "ann2"
    }

    "get person" in {
      await(personService.insert(new Person(id = None, name = "ann1", age = 3, createdAt = DateTime.now)))

      val Some(result2) = route(app, FakeRequest(GET, "/persons/ann1"))
      status(result2) mustEqual OK
      contentAsString(result2) must include("ann")
    }
  }

  def clearAllCollections: Awaitable[Unit] = {
    await(
      for {
        db <- app.injector.instanceOf[ReactiveMongoApi].database
        names <- db.collectionNames
      } yield
      names foreach (name => db.collection[BSONCollection](name).drop(failIfNotFound = false)))
  }

  def await[T](awaitable: Awaitable[T]) = {
    Await.ready(awaitable, 5 seconds)
  }

  def personService: PersonService = {
    app.injector.instanceOf[PersonService]
  }
}
