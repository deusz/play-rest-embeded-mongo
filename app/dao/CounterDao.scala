package dao

import javax.inject.Inject
import play.api.libs.concurrent.Execution.Implicits._
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson._
import scala.concurrent.Future

class CounterDao @Inject()(reactiveMongoApi: ReactiveMongoApi) {

  def countersFuture: Future[BSONCollection] = reactiveMongoApi.database.map(_.collection[BSONCollection]("counters"))

  def nextId(collection: String): Future[Long] = {
    val query = BSONDocument("collection" -> collection)
    val update = BSONDocument("$inc" -> BSONDocument("counter" -> 1))
    findAndModify(query, update)
  }

  def resetId(collection: String) : Future[Long] = {
    val query = BSONDocument("collection" -> collection)
    val update = BSONDocument("$set" -> BSONDocument("counter" -> 0))
    findAndModify(query, update)
  }

  private def findAndModify(query: BSONDocument, update: BSONDocument): Future[Long] = {
    val modify = countersFuture.map(_.BatchCommands.FindAndModifyCommand.Update(
      update,
      fetchNewObject = true,
      upsert = true))

    modify.flatMap(modify =>
      countersFuture.flatMap(_.findAndModify(query, modify) map { res =>
        res.result.get.get("counter") match {
          case Some(BSONLong(id)) => id
          case Some(BSONDouble(id)) => id.toLong
          case Some(BSONInteger(id)) => id.toLong
          case _ => 1L
        }
      })
    )
  }
}
