package models

import org.joda.time.DateTime
import play.api.libs.json.Json

case class Person(
                   override val id: Option[Long],
                   name: String,
                   age: Int,
                   createdAt: DateTime
                   ) extends IdModelLong[Person] {
}

object Person {
  implicit val jsonFormat = Json.format[Person]
}
