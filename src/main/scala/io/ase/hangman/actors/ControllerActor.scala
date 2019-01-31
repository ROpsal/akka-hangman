///
//  Author: Richard B. Opsal, Ph.D.
//
//  The game controller (supervising) actor for the Hangman game.
///


package io.ase.hangman.actors

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import io.ase.hm

object ControllerActor {
  sealed trait Command
  final case object NewGame extends Command
  final case object ExitGame extends Command

  final case class ResetRequest(hangList : List[Char], guessList : List[Char], guessSet : Set[Char]) extends Command
  final case class StatusChange(hangList : List[Char], guessList : List[Char], guessSet : Set[Char]) extends Command

	def apply(words : Seq[String]) : Behavior[Command] = Behaviors.setup(ctx => new ControllerActor(ctx, words))

  // Format strings for game status.
  private val fmtsummary = "%s  Wins : %2d  Losses : %2d"
  private val fmtinput   = "\t%s  [Guesses left : %2d ] Letter : "
}

private class ControllerActor(ctx : ActorContext[ControllerActor.Command], words : Seq[String])
    extends AbstractBehavior[ControllerActor.Command] {

  import ControllerActor._

  // Games' child actors.
  val logic: ActorRef[LogicActor.Command] = ctx.spawn(LogicActor(ctx.self), "Logic")
  val player: ActorRef[PlayerActor.Command] = ctx.spawn(PlayerActor(ctx.self, logic), "Player")

  // Tracks Hangman game progress.
  var wins   = 0
  var losses = 0

  private def printSummary(message : String, hangList : List[Char]) : Unit = {
    println("\t" + hm.wordJoin(hangList) + "\n")
    println(fmtsummary.format(message, wins, losses))
  }

  private def printGreeting() : Unit = {
    println("Type 'Exit' to leave the game, 'New' for a new game.")
    println("Good luck!\n")
    logic ! LogicActor.NewWord(hm.randomWord(words))
  }

  // On construction, send out greeting.
  printGreeting()

  override def onMessage(msg: Command): Behavior[Command] = {
    msg match {
      case NewGame =>
        printGreeting()

      case ExitGame =>
        ctx.stop(ctx.self)

      case ResetRequest(hangList, guessList, guessSet) =>
        if (hangList == guessList) wins += 1
        else if (0 < guessSet.size) losses += 1

        printSummary("Resetting Hangman game with new word.", hangList)
        ctx.self ! NewGame

      case StatusChange(hangList, guessList, guessSet) =>
        if (hangList == guessList) {
          wins += 1
          printSummary("Congratulations on your win!", hangList)
          ctx.self ! NewGame

        } else if (hm.maxGuesses == guessSet.size) {
          losses += 1
          printSummary("Too Bad! Please try again.", hangList)
          ctx.self ! NewGame

        } else {
          val guessesLeft = hm.maxGuesses - guessSet.size
          player ! PlayerActor.ReadEntry(fmtinput.format(hm.wordJoin(guessList), guessesLeft))
        }
    }
    this
  }
}