package pool

import akka.actor.{Actor, ActorLogging}

import com.typesafe.config.Config

import jodd.mail.{Email, MailServer, SendMailSession, SmtpServer}

import scala.util.control.NonFatal

case class SendEmail(licensee: Licensee) extends Product with Serializable

class Emailer(conf: Config) extends Actor with ActorLogging {
  private val smtpServer: SmtpServer = MailServer.create()
    .ssl(true)
    .host(conf.getString("email.smtp.host"))
    .auth(conf.getString("email.smtp.user"), conf.getString("email.smtp.password"))
    .buildSmtpMailServer()

  private val from = conf.getString("email.from")
  private val subject = conf.getString("email.subject")
  private val message = conf.getString("email.message")
  private val email = conf.getString("email.email")
  private val lic = conf.getString("email.lic")
  private val pin = conf.getString("email.pin")
  private val instructions = conf.getString("email.instructions")
  private val retries = conf.getInt("email.retries")

  private def buildEmail(licensee: Licensee): Email = {
    val html = s"""
                  |<!DOCTYPE html>
                  |<html lang="en">
                  |<head>
                  |<meta charset="utf-8">
                  |<title>$subject</title>
                  |</head>
                  |<body>
                  |<p>$message</p>
                  |<p>$email ${licensee.email}</p>
                  |<p>$lic ${licensee.license}</p>
                  |<p>$pin ${licensee.pin}</p>
                  |<p>$instructions</p>
                  |</body>
                  |</html>
                  |""".stripMargin
    Email.create()
      .from(from)
      .to(licensee.email)
      .subject(subject)
      .htmlMessage(html, "UTF-8")
  }

  private def sendEmail(licensee: Licensee): Option[String] = {
    var session: SendMailSession = null
    var messageId: Option[String] = None
    try {
      session = smtpServer.createSession
      session.open()
      messageId = Some( session.sendMail(buildEmail(licensee)) )
    } catch {
      case NonFatal(error) => log.error(s"*** Emailer send to: ${licensee.email} failed: ${error.getMessage}", error)
    } finally {
      session.close()
    }
    messageId
  }

  override def receive: Receive = {
    case send: SendEmail =>
      var attempts = -1
      var messageId: Option[String] = None
      while ( attempts != retries && messageId.isEmpty ) {
        messageId = sendEmail(send.licensee)
        attempts += 1
      }
      sender() ! messageId
  }
}