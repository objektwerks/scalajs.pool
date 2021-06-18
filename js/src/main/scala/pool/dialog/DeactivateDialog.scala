package pool.dialog

import com.raquo.laminar.api.L._
import pool.{Context, DeactivateLicensee, LicenseeDeactivated, ServerProxy}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object DeactivateDialog {
  val id = getClass.getSimpleName

  def apply(context: Context): Div =
    div(idAttr(id), cls("w3-modal"),
      div(cls("w3-container"),
        div(cls("w3-modal-content"),
          div(cls("w3-panel w3-indigo"),
            child.text <-- context.errors.events
          ),
          div(cls("w3-row w3-margin"),
            div(cls("w3-col"), width("15%"),
              label(cls("w3-left-align w3-text-indigo"), "License:")
            ),
            div(cls("w3-col"), width("85%"),
              input(cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("text"),
                minLength(36), maxLength(36), required(true), autoFocus(true),
                onChange.mapToValue.filter(_.nonEmpty) --> context.license
              )
            )
          ),
          div(cls("w3-row w3-margin"),
            div(cls("w3-col"), width("15%"),
              label(cls("w3-left-align w3-text-indigo"), "Email:")
            ),
            div(cls("w3-col"), width("85%"),
              input(cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("email"), required(true),
                onChange.mapToValue.filter(_.nonEmpty) --> context.email
              )
            )
          ),
          div(cls("w3-row w3-margin"),
            div(cls("w3-col"), width("15%"),
              label(cls("w3-left-align w3-text-indigo"), "Pin:")
            ),
            div(cls("w3-col"), width("85%"),
              input(cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("number"), required(true),
                onChange.mapToValue.filter(_.toIntOption.nonEmpty).map(_.toInt) --> context.pin
              )
            )
          ),
          div(cls("w3-row w3-margin"),
            button(cls("w3-btn w3-text-indigo"),
              onClick --> { _ =>
                val command = DeactivateLicensee(context.license.now(), context.email.now(), context.pin.now())
                println(s"Command: $command")
                ServerProxy.post(context.deactivateUrl, command.license, command).onComplete {
                  case Success(either) => either match {
                    case Right(event) => event match {
                      case deactivated: LicenseeDeactivated =>
                        println(s"Success: $deactivated")
                        context.licensee.set(Some(deactivated.licensee))
                        context.displayToNone(id)
                      case _ => context.errors.emit(s"Invalid: $event")
                    }
                    case Left(fault) =>
                      println(s"Fault: $fault")
                      context.errors.emit(s"Fault: $fault")
                  }
                  case Failure(failure) =>
                    println(s"Failure: $failure")
                    context.errors.emit(s"Failure: $failure")
                }
              },
              "Deactivate"
            ),
            button(cls("w3-btn w3-text-indigo"),
              onClick --> (_ => context.displayToNone(id)),
              "Cancel"
            )
          )
        )
      )
    )
}