package pool.dialog

import com.raquo.laminar.api.L._

import pool._
import pool.component.{Errors, Field, Header, Text, Label}
import pool.handler.EventHandler
import pool.menu.HomeMenu
import pool.proxy.CommandProxy
import pool.view.PoolsView

object AccountDialog {
  val id = getClass.getSimpleName
  val deactivateButtonId = id + "-deactivate-button"
  val reactivateButtonId = id + "-reactivate-button"
  val errors = new EventBus[String]

  def handler(context: Context, errors: EventBus[String], event: Event): Unit = {
    event match {
      case deactivated: Deactivated =>
        context.account.set(deactivated.account)
        context.hide(HomeMenu.poolsMenuItemId)
        context.hide(PoolsView.id)
        context.hide(id)
      case reactivated: Reactivated =>
        context.account.set(reactivated.account)
        context.hide(id)
        context.show(HomeMenu.poolsMenuItemId)
      case _ => errors.emit(s"Invalid: $event")
    }
  }

  def apply(context: Context): Div =
    div(idAttr(id), cls("w3-modal"),
      div(cls("w3-container"),
        div(cls("w3-modal-content"),
          Header("Account"),
          Errors(errors),
          Field(
            Label(column = "25%", name = "License:"),
            Text.wrapper(column = "75%", input = Text.readonly(typeOf = "text").amend {
              value <-- context.account.signal.map(_.license)
            })
          ),
          Field(
            Label(column = "25%", name = "Email:"),
            Text.wrapper(column = "75%", input = Text.readonly(typeOf = "text").amend {
              value <-- context.account.signal.map(_.email)
            })
          ),
          Field(
            Label(column = "25%", name = "Pin:"),
            Text.wrapper(column = "75%", input = Text.readonly(typeOf = "text").amend {
              value <-- context.account.signal.map(_.pin.toString)
            })
          ),
          Field(
            Label(column = "25%", name = "Activated:"),
            Text.wrapper(column = "75%", input = Text.readonly(typeOf = "text").amend {
              value <-- context.account.signal.map(_.activated.toString)
            })
          ),
          Field(
            Label(column = "25%", name = "Deactivated:"),
            Text.wrapper(column = "75%", input = Text.readonly(typeOf = "text").amend {
              value <-- context.account.signal.map(_.deactivated.toString)
            })
          ),
          div(cls("w3-bar"),
            button(idAttr(deactivateButtonId), cls("w3-bar-item w3-button w3-margin w3-text-indigo"),
              onClick --> { _ =>
                val command = Deactivate(context.account.now().license)
                val response = CommandProxy.post(context.deactivateUrl, Account.emptyLicense, command)
                EventHandler.handle(context, errors, response, handler)
              },
              "Deactivate"
            ),
            button(idAttr(reactivateButtonId), cls("w3-bar-item w3-button w3-margin w3-text-indigo"),
              onClick --> { _ =>
                val command = Reactivate(context.account.now().license)
                val response = CommandProxy.post(context.reactivateUrl, Account.emptyLicense, command)
                EventHandler.handle(context, errors, response, handler)
              },
              "Reactivate"
            ),
            button(cls("w3-bar-item w3-button w3-margin w3-text-indigo"),
              onClick --> (_ => context.hide(id)),
              "Cancel"
            )
          )
        )
      )
    )
}