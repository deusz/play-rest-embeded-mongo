package services

import javax.inject.Inject
import dao.PersonDao
import models.Person
import play.api.libs.json.JsObject
import scala.concurrent.Future

class PersonService @Inject()(personDao: PersonDao) {

  def findOne(selector: JsObject): Future[Option[Person]] = personDao.findOne(selector)

  def findById(id: Long): Future[Option[Person]] = personDao.findById(id)

  def findAll: Future[List[Person]] = personDao.findAll

  def insert(person: Person): Future[Person] = personDao.insert(person)

  def update(id: Long, update: JsObject): Future[Person] = personDao.update(id, update)
}
