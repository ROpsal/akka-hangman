///
//  Author: Richard B. Opsal, Ph.D.
//
//  The game logic actor for the Hangman game.
///


package io.ase.hangman.actors

import akka.actor.{Actor, ActorRef, Props}
import io.ase.hm


object LogicActor {
	def props(controller : ActorRef) = Props(new LogicActor(controller))

	case class NewLetter(letter : Char)
	case class NewWord(word : String)
  case object ResetRequest
}

class LogicActor(controller : ActorRef) extends Actor {

  // Import of object so we can use various case classes without scoping.
  import LogicActor._

  var hangList : List[Char] = List()
  var guessList: List[Char] = List()
  var guessSet :  Set[Char] =  Set()

  def setHangWord(word: String): Unit = {
    hangList  = hm.wordSplit(word.toUpperCase)
    guessList = hm.wordSplit("_" * word.length)
    guessSet  = Set()
  }

  override def preStart(): Unit = {
    setHangWord("HANGMAN")
  }

	def receive = {
    case NewLetter(letter : Char) =>
      if (hangList.contains(letter)) {
        guessList = hm.applyGuess(hangList, guessList, letter)
      } else {
        guessSet = guessSet + letter
      }

      controller ! ControllerActor.StatusChange(hangList, guessList, guessSet)

    case NewWord(word : String) =>
      setHangWord(word)
      controller ! ControllerActor.StatusChange(hangList, guessList, guessSet)

    case ResetRequest =>
      controller ! ControllerActor.ResetRequest(hangList, guessList, guessSet)
	}
}
