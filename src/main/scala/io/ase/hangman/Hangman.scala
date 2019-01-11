///
//  Author: Richard B. Opsal, Ph.D.
//
//  Hangman application based on three actors:
//
//    Controller
//      Player
//      Logic
///

package io.ase

package object hm {

  import java.nio.file.{Files, Paths}

  // Generate list of possible words from passed file.
  def wordList(fname : Option[String] = Option("dictionaryWords.txt")) : List[String] = {

    if (fname.isDefined && Files.isRegularFile(Paths.get(fname.get))) {
      val source = scala.io.Source.fromFile(fname.get)
      val words: List[String] = source.getLines.toList
      source.close()
      words
    } else {
      Nil
    }
  }

  // Return a random word from the passed list.
  def randomWord(words : Seq[String]) : String = {
    words( scala.util.Random.nextInt(words.length) )
  }

  // Split the word into individual letters.
  def wordSplit(word : String) : List[Char] = {
    word.toList
  }

  // Join the list of characters together with a space in-between.
  def wordJoin(wordlist : List[Char]) : String = {
    wordlist.mkString(" ")
  }

  // Set of upper case letters.
  def alphaSet : Set[Char] = {
    ('A' to 'Z').toSet
  }

  // Generate a new guess list based on letter, current matches and actual word.
  def applyGuess(hangList : List[Char], guessList : List[Char], letter : Char) : List[Char] = {
    guessList.zip(hangList).map({case(g,h) => if (letter == h) h else g})
  }

  // Maximum guess count for Hangman.
  def maxGuesses : Int = 6

  // Locate the default dictionary file.
  def defaultFile : Option[String] = {
    val defaultName = "dictionaryWords.txt"
    val searchPaths = List("src/main/resources", "../resources", "resources")

    val possiblePath : Option[String] = searchPaths find (i =>
      Files.isRegularFile(Paths.get(i, defaultName)))

    if (possiblePath.isDefined) Option(Paths.get(possiblePath.get, defaultName).toString) else None
  }
}


///
// The Hangman application.
///

object HangmanMain {

  import akka.actor.typed.scaladsl.Behaviors
  import akka.actor.typed.{ ActorSystem, Behavior, PostStop, Terminated }

  import io.ase.hangman.actors.ControllerActor

  sealed trait Command

  def main(args: Array[String]): Unit = {
    // Cheery intro to the Hangman game.
    println("Welcome to the Akka Hangman word guessing game.")

    // Obtain list of words to guess from.
    val fname : Option[String] = if (args.isEmpty) hm.defaultFile else Option(args(0))
    val words : Seq[String] = hm.wordList(fname)

    if (words.isEmpty) {
      // Can't play without a word list!
      println("\nHangman word list not loaded for game.  Sorry!")

    } else {
      // Start the game by starting the ActorSystem.
      ActorSystem(HangmanMain(words), "hangman")
    }
  }

  def apply(words : Seq[String]): Behavior[Command] = Behaviors.setup { context =>
    val controller = context.spawn(ControllerActor(words), "controller")

    context.watch(controller)
    Behaviors.receiveSignal {
      case (_, Terminated(_)) =>
        // We're terminated.
        Behaviors.stopped

      case (_, PostStop) =>
        // Leave a cheery outro to the Hangman game.
        println("\nThank you for playing Akka Hangman!")
        Behaviors.same
    }
  }
}