package dao

import javax.inject.Inject
import models.Person
import play.api.libs.json.Json._
import play.api.libs.json.{JsObject, Json}
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.ReadPreference
import reactivemongo.play.json.collection.JSONCollection
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class PersonDaoException(message: String) extends RuntimeException(message)

class PersonDao @Inject()(val reactiveMongoApi: ReactiveMongoApi, counterDao: CounterDao)
  extends ReactiveMongoComponents with MongoController {

  def personsFuture: Future[JSONCollection] = database.map(_.collection[JSONCollection](PersonDao.CollectionName))

  def findOne(selector: JsObject = obj()): Future[Option[Person]] =
    personsFuture.flatMap(
      _.find(selector).one[Person]
    )

  def findById(id: Long): Future[Option[Person]] =
    findOne(obj("id" -> id))

  def findAll: Future[List[Person]] = personsFuture.flatMap(
    _.find(Json.obj()).
      cursor[Person](ReadPreference.primary).
      collect[List]()
  )

  def insert(person: Person): Future[Person] =
    for {
      thePerson <- personWithId(person)
      persons <- personsFuture
      lastError <- persons.insert(thePerson)
      inserted <- findById(thePerson.id.get) if lastError.ok
    } yield

    inserted match {
      case Some(result) => result
      case None => throw PersonDaoException(s"Cannot insert new person")
    }

  def update(id: Long, update: JsObject): Future[Person] =
    for {
      persons <- personsFuture
      lastError <- persons.update(obj("id" -> id), update)
      updated <- findById(id) if lastError.ok
    } yield

    updated match {
      case Some(result) => result
      case None => throw PersonDaoException(s"Cannot update person id=$id")
    }

  private def personWithId(person: Person) = {
    nextId map { newId =>
      person.copy(
        id = Some(newId),
        name = person.name,
        age = person.age,
        createdAt = person.createdAt
      )
    }
  }

  private def nextId: Future[Long] = counterDao.nextId(PersonDao.CollectionName)
}

object PersonDao {
  val CollectionName = "persons"
}
