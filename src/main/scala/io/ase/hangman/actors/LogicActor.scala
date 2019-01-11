///
//  Author: Richard B. Opsal, Ph.D.
//
//  The game logic actor for the Hangman game.
///


package io.ase.hangman.actors

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, Behaviors}
import io.ase.hm


object LogicActor {
  sealed trait Command
	case class NewLetter(letter : Char) extends Command
	case class NewWord(word : String) extends Command
  case object ResetRequest extends Command

	def apply(controller : ActorRef[ControllerActor.Command]) : Behavior[Command] =
    Behaviors.setup(ctx => new LogicActor(controller))
}

private class LogicActor(controller : ActorRef[ControllerActor.Command]) extends AbstractBehavior[LogicActor.Command] {

  var hangList : List[Char] = List()
  var guessList: List[Char] = List()
  var guessSet :  Set[Char] =  Set()

  def setHangWord(word: String): Unit = {
    hangList  = hm.wordSplit(word.toUpperCase)
    guessList = hm.wordSplit("_" * word.length)
    guessSet  = Set()
  }

  // Construction initialization.
  setHangWord("HANGMAN")

  import LogicActor._
  override def onMessage(msg: Command): Behavior[Command] = {
    msg match {
      case NewLetter(letter: Char) =>
        if (hangList.contains(letter)) {
          guessList = hm.applyGuess(hangList, guessList, letter)
        } else {
          guessSet = guessSet + letter
        }

        controller ! ControllerActor.StatusChange(hangList, guessList, guessSet)

      case NewWord(word: String) =>
        setHangWord(word)
        controller ! ControllerActor.StatusChange(hangList, guessList, guessSet)

      case ResetRequest =>
        controller ! ControllerActor.ResetRequest(hangList, guessList, guessSet)
    }
    this
  }
}