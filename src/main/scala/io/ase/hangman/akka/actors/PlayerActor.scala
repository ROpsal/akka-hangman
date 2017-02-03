///
//  Author: Richard B. Opsal, Ph.D.
//
//  The player logic actor for the Hangman game.
///


package io.ase.hangman.akka.actors

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import akka.actor.{Actor, ActorRef, Props}
import io.ase.hangman.akka.hm

object PlayerActor {
	def props(logic : ActorRef) = Props(classOf[PlayerActor], logic)

	case class NewEntry(message : String, entry : String)
  case class ReadEntry(message : String)
}

class PlayerActor(logic : ActorRef) extends Actor {

  private val alphaSet = hm.alphaSet

  def receive = {
		case PlayerActor.NewEntry(message : String, entry : String) => {
			entry.toUpperCase match {
        case letter if (1 == letter.length) && alphaSet.contains(letter(0)) => logic ! LogicActor.NewLetter(letter(0))
        case "NEW" => logic ! LogicActor.ResetRequest
        case "EXIT" => context.parent ! ControllerActor.ExitGame
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
