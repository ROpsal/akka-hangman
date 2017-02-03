///
//  Author: Richard B. Opsal, Ph.D.
//
//  Hangman application based on three actors:
//
//    Controller
//      Player
//      Logic
///

package io.ase.hangman.akka

package object hm {

  // Generate list of possible words from passed file.
  def wordList(fname : String = "dictionaryWords.txt") : List[String] = {
    val source = scala.io.Source.fromFile( fname )
    val words : List[String] = source.getLines.toList
    source.close()
    words
  }

  // Return a random word from the passed list.
  def randomWord(words : List[String]) : String = {
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
}


///
// The Hangman application.
///

object Hangman extends App {

  // Cheery intro to the Hangman game.
  println("Welcome to the Akka Hangman word guessing game.")

  // List of words to guess from.
  val fname = if (args.isEmpty) "src/resources/dictionaryWords.txt" else args(0)
  val words = hm.wordList(fname)

  // Start using Akka.
  import akka.actor.ActorSystem
  import io.ase.hangman.akka.actors.ControllerActor

  val system = ActorSystem()
  val controller = system.actorOf(ControllerActor.props(words), "Controller")

  import scala.concurrent.Await
  import scala.concurrent.duration.Duration
  Await.result(system.whenTerminated, Duration.Inf)

  // Cheery outro to the Hangman game.
  println("\nThank you for playing Akka Hangman!")
}
