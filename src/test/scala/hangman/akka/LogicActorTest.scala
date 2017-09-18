///
//  Author: Richard B. Opsal, Ph.D.
//
//  Exercise (test) the logic actor for the Hangman game.
///

package hangman.akka

import language.postfixOps
import scala.concurrent.duration._

import akka.testkit.{TestActorRef, TestKit, TestProbe}
import akka.actor.ActorSystem

import org.scalatest.{ WordSpecLike, MustMatchers }

import io.ase.hangman.akka.hm
import io.ase.hangman.akka.actors.{ControllerActor, LogicActor}

class LogicActorTest extends TestKit(ActorSystem("test-system"))
  with WordSpecLike
  with MustMatchers
  with StopSystemAfterAll {

  val controller = TestProbe()
  val logicRef = TestActorRef[LogicActor](LogicActor.props(controller.ref), "Logic")

  val word = "AMAZING"
  var hangList: List[Char] = hm.wordSplit(word.toUpperCase)
  var guessList: List[Char] = hm.wordSplit("_" * word.length)
  var guessSet: Set[Char] = Set()

  def checkEquality(_hangList: List[Char], _guessList: List[Char], _guessSet: Set[Char]): Unit = {
    hangList  mustEqual _hangList
    guessList mustEqual _guessList
    guessSet  mustEqual _guessSet
  }

  "The Logic actor" should {
    "send a ControllerActor.StatusChange to the Controller actor" when {
      "the LogicActor.NewWord value is changed." in {

        logicRef ! LogicActor.NewWord(word)
        controller.expectMsgPF(500 millis) {
          case ControllerActor.StatusChange(_hangList, _guessList, _guessSet) => {
            checkEquality(_hangList, _guessList, _guessSet)
          }
        }
      }

      "the LogicActor.NewLetter value is matched." in {

        val letter = 'Z'
        guessList = hm.applyGuess(hangList, guessList, letter)
        if (!hangList.contains(letter)) guessSet = guessSet + letter

        logicRef ! LogicActor.NewLetter(letter)
        controller.expectMsgPF(500 millis) {
          case ControllerActor.StatusChange(_hangList, _guessList, _guessSet) => {
            checkEquality(_hangList, _guessList, _guessSet)
          }
        }
      }

      "the LogicActor.NewLetter value is not matched." in {

        val letter = 'X'
        guessList = hm.applyGuess(hangList, guessList, letter)
        if (!hangList.contains(letter)) guessSet = guessSet + letter

        logicRef ! LogicActor.NewLetter(letter)
        controller.expectMsgPF(500 millis) {
          case ControllerActor.StatusChange(_hangList, _guessList, _guessSet) => {
            checkEquality(_hangList, _guessList, _guessSet)
          }
        }
      }

    }

    "send a ControllerActor.ResetRequest to the Controller actor" when {
      "the LogicActor.ResetRequest is received." in {

        logicRef ! LogicActor.ResetRequest
        controller.expectMsgPF(500 millis) {
          case ControllerActor.ResetRequest(_hangList, _guessList, _guessSet) => {
            checkEquality(_hangList, _guessList, _guessSet)
          }
        }
      }
    }
  }
}
