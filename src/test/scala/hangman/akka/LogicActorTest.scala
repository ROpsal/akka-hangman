///
//  Author: Richard B. Opsal, Ph.D.
//
//  Exercise (test) the logic actor for the Hangman game.
///

package hangman.akka

import language.postfixOps
import scala.concurrent.duration._

import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import akka.actor.ActorSystem

import org.scalatest.{ WordSpecLike }

import io.ase.hangman.akka.hm
import io.ase.hangman.akka.actors.{ControllerActor, LogicActor}

class LogicActorTest extends TestKit(ActorSystem("test-system"))
  with WordSpecLike
  with StopSystemAfterAll {

  val controller = TestProbe()
  val logicRef = TestActorRef[LogicActor](LogicActor.props(controller.ref), "Logic")
  val logicActor = logicRef.underlyingActor

  val word = "AMAZING"
  var hangList : List[Char] = hm.wordSplit(word.toUpperCase)
  var guessList: List[Char] = hm.wordSplit("_" * word.length)
  var guessSet :  Set[Char] = Set()

  def checkEquality() : Unit = {
    assert(hangList == logicActor.hangList)
    assert(guessList == logicActor.guessList)
    assert(guessSet == logicActor.guessSet)
  }


  "The Logic actor" should {
    "send a ControllerActor.StatusChange to the Controller actor" when {
      "the LogicActor.NewWord value is changed." in {

        logicRef ! LogicActor.NewWord(word)
        controller.expectMsg(500 millis, ControllerActor.StatusChange(hangList, guessList, guessSet))
        checkEquality()
      }

      "the LogicActor.NewLetter value is matched." in {

        val letter = 'Z'
        guessList = hm.applyGuess(hangList, guessList, letter)

        logicRef ! LogicActor.NewLetter(letter)
        controller.expectMsg(500 millis, ControllerActor.StatusChange(hangList, guessList, guessSet))
        checkEquality()
      }

      "the LogicActor.NewLetter value is not matched." in {

        val letter = 'X'
        guessList = hm.applyGuess(hangList, guessList, letter)
        guessSet = guessSet + letter

        logicRef ! LogicActor.NewLetter(letter)
        controller.expectMsg(500 millis, ControllerActor.StatusChange(hangList, guessList, guessSet))
        checkEquality()
      }
    }

    "send a ControllerActor.ResetRequest to the Controller actor" when {
      "the LogicActor.ResetRequest is received." in {

        logicRef ! LogicActor.ResetRequest
        controller.expectMsg(500 millis, ControllerActor.ResetRequest(hangList, guessList, guessSet))
        checkEquality()
      }
    }
  }
}
