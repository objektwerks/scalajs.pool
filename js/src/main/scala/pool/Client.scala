package pool

import com.raquo.laminar.api.L._

import org.scalajs.dom._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("Client")
class Client(publicUrl: String, apiUrl: String) extends js.Object {
  ServiceWorker.register()

  val context = Context(publicUrl, apiUrl)
  val model = Model()
  var root = render(document.getElementById("client"), renderHome)

  def renderHome: Div =
    div(
      renderNavigation,
      renderNow
    )

  def renderCenter(center: Div): Unit = {
    root.unmount()
    root = render(document.getElementById("client"), center)
  }

  def renderNavigation: Div =
    div(
      cls("w3-bar w3-white w3-text-indigo"),
      a( href("#"), onClick --> (_ => renderCenter( renderRegister) ), cls("w3-bar-item w3-button"), "Register" ),
      a( href("#"), onClick --> (_ => renderCenter( renderLogin) ), cls("w3-bar-item w3-button"), "Login" )
    )

  def renderNow: Div = {
    val datetimeVar = Var("")
    ServerProxy.post(s"$publicUrl/now").foreach(now => datetimeVar.set(now.stripPrefix("\"").stripSuffix("\"")) )
    div(
      label( cls("w3-text-indigo"), fontSize("12px"), child.text <-- datetimeVar )
    )
  }

  def renderRegister: Div =
    div( cls("w3-container w3-padding-16"),
      div( cls("w3-row"),
        div( cls("w3-col"), width("15%"),
          label( cls("w3-left-align w3-text-indigo"), "Email:" )
        ),
        div( cls("w3-col"), width("85%"),
          input( cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("email"), required(true), autoFocus(true),
            onChange.mapToValue.filter(_.nonEmpty) --> model.email
          )
        )
      ),
      div( cls("w3-row w3-padding-16"),
        button( cls("w3-btn w3-text-indigo"),
          onClick.mapTo( SignUp(model.email.now()) ) --> context.commandObserver,
          "Register"
        )
      )
    )

  def renderLogin: Div =
    div( cls("w3-container w3-padding-16"),
      div( cls("w3-row"),
        div( cls("w3-col"), width("15%"),
          label( cls("w3-left-align w3-text-indigo"), "Email:" )
        ),
        div( cls("w3-col"), width("85%"),
          input( cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("email"), required(true), autoFocus(true),
            onChange.mapToValue.filter(_.nonEmpty) --> model.email
          )
        )
      ),
      div( cls("w3-row"),
        div( cls("w3-col"), width("15%"),
          label( cls("w3-left-align w3-text-indigo"), "Pin:" )
        ),
        div( cls("w3-col"), width("85%"),
          input( cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("number"), required(true),
            onChange.mapToValue.filter(_.toIntOption.nonEmpty).map(_.toInt) --> model.pin
          )
        )
      ),
      div( cls("w3-row w3-padding-16"),
        button( cls("w3-btn w3-text-indigo"),
          onClick.mapTo( SignIn(model.email.now(), model.pin.now()) ) --> context.commandObserver,
          "Login"
        )
      )
    )
}