///
//  Author: Richard B. Opsal, Ph.D.
//
//  The game controller (supervising) actor for the Hangman game.
///


package io.ase.hangman.akka.actors

import akka.actor.{Actor, PoisonPill, Props}
import io.ase.hangman.akka.hm

object ControllerActor {
	def props(words : Seq[String]) = Props(classOf[ControllerActor], words)

  case object NewGame
  case object ExitGame

  case class ResetRequest(hangList : List[Char], guessList : List[Char], guessSet : Set[Char])
  case class StatusChange(hangList : List[Char], guessList : List[Char], guessSet : Set[Char])
}

class ControllerActor(val words : List[String]) extends Actor {

  // Tracks Hangman game progress.
  var wins   = 0
  var losses = 0

  // Formatting strings for nice output.
  val fmtsummary = "%s  Wins : %2d  Losses : %2d"
  val fmtinput   = "\t%s  [Guesses left : %2d ] Letter : "

  private def printSummary(message : String, hangList : List[Char]) : Unit = {

    println("\t" + hm.wordJoin(hangList) + "\n")
    println(fmtsummary.format(message, wins, losses))
  }

  override def preStart() : Unit = {
    println("Type 'Exit' to leave the game, 'New' for a new game.")
    println("Good luck!\n")
    logic ! LogicActor.NewWord(hm.randomWord(words))
  }

  // Games' child actors.
  private val logic  = context.actorOf(LogicActor.props(self), "Logic")
  private val player = context.actorOf(PlayerActor.props(self, logic), "Player")

  def receive = {
    case ControllerActor.NewGame =>
      preStart()

    case ControllerActor.ExitGame => {
      context.parent ! PoisonPill
    }

    case ControllerActor.ResetRequest(hangList, guessList, guessSet) => {
      if (hangList == guessList) wins += 1
      else if (0 < guessSet.size) losses += 1

      printSummary("Resetting Hangman game with new word.", hangList)
      self ! ControllerActor.NewGame
    }

    case ControllerActor.StatusChange(hangList, guessList, guessSet) => {
      if (hangList == guessList) {
        wins += 1
        printSummary("Congratulations on your win!", hangList)
        self ! ControllerActor.NewGame

      } else if (hm.maxGuesses == guessSet.size) {
        losses += 1
        printSummary("Too Bad! Please try again.", hangList)
        self ! ControllerActor.NewGame

      } else {
        val guessesLeft = hm.maxGuesses - guessSet.size
        player ! PlayerActor.ReadEntry(fmtinput.format(hm.wordJoin(guessList), guessesLeft))

      }
    }
	}
}
