package tripletail

import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import org.scalajs.dom.ext.Ajax

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PoolRestClient(serverUrl: String) {
  val headers = Map("Content-Type" -> "application/json; charset=utf-8", "Accept" -> "application/json")

  def post(path: String, command: Command): Future[Either[Fault, Event]] = {
    Ajax.post(url = serverUrl + path, headers = headers, data = command.asJson.toString).map { xhr =>
      xhr.status match {
        case 200 => decode[Event](xhr.responseText).fold(e => Left(Fault.toFault(e)), v => Right(v))
        case _ => Left(Fault.toFault(xhr.status))
      }
    }.recover { case e => Left(Fault.toFault(e)) }
  }

  def post(path: String, license: String, entity: Entity): Future[Either[Fault, State]] = {
    val headersWithLicense = headers ++: Map("license" -> license)
    Ajax.post(url = serverUrl + path, headers = headersWithLicense, data = entity.asJson.toString).map { xhr =>
      xhr.status match {
        case 200 => decode[State](xhr.responseText).fold(e => Left(Fault.toFault(e)), v => Right(v))
        case _ => Left(Fault.toFault(xhr.status))
      }
    }.recover { case e => Left(Fault.toFault(e)) }
  }
}

object PoolRestClient {
  def apply(serverUrl: String): PoolRestClient = new PoolRestClient(serverUrl)
}