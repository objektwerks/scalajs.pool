package pool.dialog

import com.raquo.laminar.api.L._

import pool.{Context, Licensee, ServerProxy, SignUp, SignedUp}

import scala.concurrent.ExecutionContext.Implicits.global

object RegisterDialog {
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
          div( cls("w3-row w3-margin"),
            div( cls("w3-col"), width("15%"),
              label( cls("w3-left-align w3-text-indigo"), "Email:" )
            ),
            div( cls("w3-col"), width("85%"),
              input( cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("email"), required(true), autoFocus(true),
                onChange.mapToValue.filter(_.nonEmpty) --> context.email
              )
            )
          ),
          div( cls("w3-row w3-margin"),
            button( cls("w3-btn w3-text-indigo"),
              onClick --> {_ =>
                val command = SignUp(context.email.now())
                ServerProxy.post(context.signupUrl, Licensee.emptyLicense, command).foreach {
                  case Right(event) => event match {
                    case signedup: SignedUp =>
                      println(s"Success: $signedup")
                      statusEventBus.emit( s"Success: $signedup" )
                      context.licensee.set( Some(signedup.licensee) )
                      context.displayToNone(id)
                    case _ => statusEventBus.emit( s"Invalid: $event" )
                  }
                  case Left(fault) =>
                    println(s"Failure: $fault")
                    statusEventBus.emit( s"Failure: $fault" )
                }
               },
              "Register"
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