///
//  Author: Richard B. Opsal, Ph.D.
//
//  Exercise (test) the logic actor for the Hangman game.
///

package hangman.akka

import language.postfixOps
import scala.concurrent.duration._
import akka.actor.testkit.typed.scaladsl.{ ActorTestKit, TestProbe }
import akka.actor.typed.ActorRef
import org.scalatest.{ BeforeAndAfterAll, matchers, wordspec }
import io.ase.hm
import io.ase.hangman.actors.{ControllerActor, LogicActor}

class LogicActorTest extends wordspec.AnyWordSpec with matchers.must.Matchers with BeforeAndAfterAll  {

  val testKit: ActorTestKit = ActorTestKit()
  val controller: TestProbe[ControllerActor.Command] = testKit.createTestProbe[ControllerActor.Command]
  val logicRef: ActorRef[LogicActor.Command] = testKit.spawn(LogicActor(controller.ref), "Logic")

  override def afterAll(): Unit = testKit.shutdownTestKit()

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
        controller.expectMessageType[ControllerActor.StatusChange](500 millis) match {
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
        controller.expectMessageType[ControllerActor.StatusChange](500 millis) match {
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
        controller.expectMessageType[ControllerActor.StatusChange](500 millis) match {
          case ControllerActor.StatusChange(_hangList, _guessList, _guessSet) => {
            checkEquality(_hangList, _guessList, _guessSet)
          }
        }
      }

    }

    "send a ControllerActor.ResetRequest to the Controller actor" when {
      "the LogicActor.ResetRequest is received." in {

        logicRef ! LogicActor.ResetRequest
        controller.expectMessageType[ControllerActor.ResetRequest](500 millis) match {
          case ControllerActor.ResetRequest(_hangList, _guessList, _guessSet) => {
            checkEquality(_hangList, _guessList, _guessSet)
          }
        }
      }
    }
  }
}
