import Entity._

import doobie._
import doobie.implicits._
import cats.effect.IO

import scala.concurrent.ExecutionContext

object DoobieExercise extends App {
  val userRepositoryImplByDoobie = new UserRepositoryImplByDoobie
  val res                        = userRepositoryImplByDoobie.findById(Id(1000)).unsafeRunSync()
  val res2                       = userRepositoryImplByDoobie.findById(Id(1)).unsafeRunSync()
  val res3                       = userRepositoryImplByDoobie.save(User(Name("fook"), Id(1233))).unsafeRunSync()
  println(res)
  println(res2)
  println(res3)
}

object Entity {
  case class User(name: Name, id: Id)
  case class Name(value: String)
  case class Id(value: Long)
}

import Entity._

trait UserRepository[F[_]] {
  def findById(id: Id): F[Option[User]]
  def save(user: User): F[User]
}

class UserRepositoryImplByDoobie extends UserRepository[IO] {

  implicit val cs = IO.contextShift(ExecutionContext.global)

  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql:doobietest",
    "kodai",
    ""
  )

  def findById(id: Id): IO[Option[User]] =
    sql"select name, id from users where id = ${id.value}"
      .query[User]
      .option
      .transact(xa)

  def save(user: User): IO[User] =
    {
    for {
      _    <- sql"insert into users (name, id) values (${user.name.value}, ${user.id.value})".update.run
      user <- sql"select name, id from users where id = ${user.id.value}".query[User].unique
    } yield user
  }.transact(xa)

}
