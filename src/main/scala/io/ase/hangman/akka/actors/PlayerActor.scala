///
//  Author: Richard B. Opsal, Ph.D.
//
//  The player input actor for the Hangman game.
///


package io.ase.hangman.akka.actors

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import io.ase.hangman.akka.hm

object PlayerActor {
	def props(controller : ActorRef, logic : ActorRef) = Props(new PlayerActor(controller, logic))

	case class NewEntry(message : String, entry : String)
  case class ReadEntry(message : String)
}

class PlayerActor(controller : ActorRef, logic : ActorRef) extends Actor with akka.actor.ActorLogging {

  private val alphaSet = hm.alphaSet

  def receive = {
		case PlayerActor.NewEntry(message : String, entry : String) => {
			entry.toUpperCase match {
        case letter if (1 == letter.length) && alphaSet.contains(letter(0)) => logic ! LogicActor.NewLetter(letter(0))
             log.info("Letter {}", letter)
        case "NEW" => logic ! LogicActor.ResetRequest
        case "EXIT" => controller ! ControllerActor.ExitGame
        case _ => self ! PlayerActor.ReadEntry(message)
      }
    }

    case PlayerActor.ReadEntry(message : String) => {
      val future : Future[String] = Future {
        scala.io.StdIn.readLine(message)
      }

      future map {
        entry => self ! PlayerActor.NewEntry(message, entry)
      }
    }
	}
}
