///
//  Author: Richard B. Opsal, Ph.D.
//
//  Exercise (test) the player input actor for the Hangman game.
///

package hangman.akka

import language.postfixOps
import scala.concurrent.duration._
import akka.actor.testkit.typed.scaladsl.{ ActorTestKit, TestProbe }
import akka.actor.typed.ActorRef
import org.scalatest.{ BeforeAndAfterAll, MustMatchers, WordSpec }
import io.ase.hangman.actors.{ ControllerActor, LogicActor, PlayerActor }

class PlayerActorTest extends WordSpec with MustMatchers with BeforeAndAfterAll  {

  val testKit: ActorTestKit = ActorTestKit()
  val controller: TestProbe[ControllerActor.Command] = testKit.createTestProbe[ControllerActor.Command]
  val logic: TestProbe[LogicActor.Command] = testKit.createTestProbe[LogicActor.Command]
  val player: ActorRef[PlayerActor.Command] = testKit.spawn(PlayerActor(controller.ref, logic.ref), "Player")

  override def afterAll(): Unit = testKit.shutdownTestKit()

  val dummyPrompt = "A dummy prompt : "

  "The Player actor" should {
    "send a LogicActor.ResetRequest to the Logic actor" when {
      "the PlayerActor.NewEntry value is 'new'" in {

        player ! PlayerActor.NewEntry(dummyPrompt, "new")
        logic.expectMessage[LogicActor.ResetRequest.type](500 millis, LogicActor.ResetRequest)
      }
    }

    "send a ControllerActor.ExitGame to the Controller actor" when {
      "the PlayerActor.NewEntry value is 'exit'" in {

        player ! PlayerActor.NewEntry(dummyPrompt, "exit")
        controller.expectMessage[ControllerActor.ExitGame.type](500 millis, ControllerActor.ExitGame)
      }
    }

    "send a LogicActor.NewLetter to the Logic actor" when {
      "the PlayerActor.NewEntry value is an letter in the range A..Z." in {

        player ! PlayerActor.NewEntry(dummyPrompt, "a")
        logic.expectMessage[LogicActor.NewLetter](500 millis, LogicActor.NewLetter('A'))
      }
    }
  }
}
