///
//  Author: Richard B. Opsal, Ph.D.
//
//  Exercise (test) the player input actor for the Hangman game.
///

package hangman.akka

import language.postfixOps
import scala.concurrent.duration._
import akka.testkit.{ TestKit, TestProbe }

import akka.actor.{ ActorSystem }
import org.scalatest.{ WordSpecLike }

import io.ase.hangman.actors.{ ControllerActor, LogicActor, PlayerActor }

class PlayerActorTest extends TestKit(ActorSystem("test-system"))
  with WordSpecLike
  with StopSystemAfterAll {

  val controller = TestProbe()
  val logic = TestProbe()
  val player = system.actorOf(PlayerActor.props(controller.ref, logic.ref), "Player")


  val dummyPrompt = "A dummy prompt : "

  "The Player actor" should {
    "send a LogicActor.ResetRequest to the Logic actor" when {
      "the PlayerActor.NewEntry value is 'new'" in {

        player ! PlayerActor.NewEntry(dummyPrompt, "new")
        logic.expectMsg(500 millis, LogicActor.ResetRequest)
      }
    }

    "send a ControllerActor.ExitGame to the Controller actor" when {
      "the PlayerActor.NewEntry value is 'exit'" in {

        player ! PlayerActor.NewEntry(dummyPrompt, "exit")
        controller.expectMsg(500 millis, ControllerActor.ExitGame)
      }
    }

    "send a LogicActor.NewLetter to the Logic actor" when {
      "the PlayerActor.NewEntry value is an letter in the range A..Z." in {

        player ! PlayerActor.NewEntry(dummyPrompt, "a")
        logic.expectMsg(500 millis, LogicActor.NewLetter('A'))
      }
    }

  }
}
