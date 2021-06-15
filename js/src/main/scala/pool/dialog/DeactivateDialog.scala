package pool.dialog

import com.raquo.laminar.api.L._

import pool.{Context, DeactivateLicensee, LicenseeDeactivated, ServerProxy}

import scala.concurrent.ExecutionContext.Implicits.global

object DeactivateDialog {
  val id = getClass.getSimpleName
  val statusEventBus = new EventBus[String]

  def apply(context: Context): Div =
    div( idAttr(id), cls("w3-modal"),
      div( cls("w3-container"),
        div( cls("w3-modal-content"),
          div( cls("w3-panel w3-indigo"),
            label( cls("w3-left-align"), "Status:" ),
            child.text <-- statusEventBus.events
          ),
          div( cls("w3-row"),
            div( cls("w3-col"), width("15%"),
              label( cls("w3-left-align w3-text-indigo"), "License:" )
            ),
            div( cls("w3-col"), width("85%"),
              input( cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("text"),
                minLength(36), maxLength(36), required(true), autoFocus(true),
                onChange.mapToValue.filter(_.nonEmpty) --> context.model.license
              )
            )
          ),
          div( cls("w3-row"),
            div( cls("w3-col"), width("15%"),
              label( cls("w3-left-align w3-text-indigo"), "Email:" )
            ),
            div( cls("w3-col"), width("85%"),
              input( cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("email"), required(true),
                onChange.mapToValue.filter(_.nonEmpty) --> context.model.email
              )
            )
          ),
          div( cls("w3-row"),
            div( cls("w3-col"), width("15%"),
              label( cls("w3-left-align w3-text-indigo"), "Pin:" )
            ),
            div( cls("w3-col"), width("85%"),
              input( cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("number"), required(true),
                onChange.mapToValue.filter(_.toIntOption.nonEmpty).map(_.toInt) --> context.model.pin
              )
            )
          ),
          div( cls("w3-row w3-padding-16"),
            button( cls("w3-btn w3-text-indigo"),
              onClick --> {_ =>
                val command = DeactivateLicensee(context.model.license.now(), context.model.email.now(), context.model.pin.now())
                ServerProxy.post(context.urls.deactivate, command.license, command).foreach {
                  case Right(event) => event match {
                    case deactivated: LicenseeDeactivated =>
                      statusEventBus.emit( s"Success: $deactivated" )
                      context.model.licensee.set( Some(deactivated.licensee) )
                      context.displayToNone(id)
                    case _ => statusEventBus.emit( s"Invalid: $event" )
                  }
                  case Left(fault) => statusEventBus.emit( s"Failure: $fault" )
                }
              },
              "Deactivate"
            ),
            button( cls("w3-btn w3-text-indigo"),
              onClick --> (_ => context.displayToNone(id) ),
              "Cancel"
            )
          )
        )
      )
    )
}