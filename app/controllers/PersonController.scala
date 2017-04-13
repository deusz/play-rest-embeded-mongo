package controllers

import javax.inject._

import models.Person
import org.joda.time.DateTime
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json._
import play.api.libs.json._
import play.api.mvc._
import services.PersonService
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class PersonController @Inject()(personService: PersonService) extends Controller {
  private val logger = Logger(this.getClass)

  case class WritePerson(name: String, age: Int) {
    def toPerson = new Person(id = None, name = name, age = age, createdAt = DateTime.now)
  }

  val form = Form(
    mapping(
      "name" -> text,
      "age" -> number
    )(WritePerson.apply)(WritePerson.unapply)
  )

  def create = Action.async { implicit request =>
    form.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest("Error")),
      writePerson =>
        personService.insert(writePerson.toPerson).map(person =>
          Ok(Json.toJson(person))
        )
    )
  }

  def findByName(name: String) = Action.async {
    personService.findOne(Json.obj("name" -> name)).map{
      case Some(person) => Ok(Json.toJson(person))
      case _            => NotFound
    }
  }
}
