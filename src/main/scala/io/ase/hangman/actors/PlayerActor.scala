///
//  Author: Richard B. Opsal, Ph.D.
//
//  The player input actor for the Hangman game.
///


package io.ase.hangman.actors

import akka.actor.typed.{ ActorRef, Behavior }
import akka.actor.typed.scaladsl.Behaviors

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import io.ase.hm

object PlayerActor {
  sealed trait Command
	case class NewEntry(message : String, entry : String) extends Command
  case class ReadEntry(message : String) extends Command

  private val alphaSet = hm.alphaSet

	def apply(controller : ActorRef[ControllerActor.Command], logic : ActorRef[LogicActor.Command]) : Behavior[Command] =
    Behaviors.setup { ctx =>
      Behaviors.receiveMessagePartial {
        case NewEntry(message: String, entry: String) =>
          entry.toUpperCase match {
            case letter if (1 == letter.length) && alphaSet.contains(letter(0)) =>
              ctx.log.info("Letter {}", letter)
              logic ! LogicActor.NewLetter(letter(0))
            case "NEW" => logic ! LogicActor.ResetRequest
            case "EXIT" => controller ! ControllerActor.ExitGame
            case _ => ctx.self ! ReadEntry(message)
          }
          Behaviors.same

        case ReadEntry(message: String) =>
          Future {
            scala.io.StdIn.readLine(message)
          } map { entry =>
            ctx.self ! NewEntry(message, entry)
          }
          Behaviors.same
      }
    }
}