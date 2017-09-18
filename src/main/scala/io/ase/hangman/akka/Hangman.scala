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

  // Locate the default dictionary file.
  def defaultFile : Option[String] = {
    val defaultName = "dictionaryWords.txt"
    val searchPaths = List("src/main/resources", "../resources", "resources")

    val possiblePath : Option[String] = searchPaths find (i =>
      Files.isRegularFile(Paths.get(i, defaultName)))

    if (possiblePath.isDefined) println(Option(Paths.get(possiblePath.get, defaultName).toString))
    if (possiblePath.isDefined) Option(Paths.get(possiblePath.get, defaultName).toString) else None
  }
}


///
// The Hangman application.
///

object Hangman extends App {

  // Cheery intro to the Hangman game.
  println("Welcome to the Akka Hangman word guessing game.")

  // List of words to guess from.
  val fname : Option[String] = if (args.isEmpty) hm.defaultFile else Option(args(0))
  val words : Seq[String] = hm.wordList(fname)

  if (words.isEmpty) {
    // Can't play with word list!
    println("\nHangman word list not loaded for game.  Sorry!")

  } else {
    // Startup Akka system.
    import akka.actor.ActorSystem
    import io.ase.hangman.akka.actors.ControllerActor

    // Implicitly used by the Akka actors.  Both are needed!
    val system = ActorSystem()
    val controller = system.actorOf(ControllerActor.props(words), "Controller")
    val config = system.settings.config

    // Wait on actor system to terminate itself.
    import scala.concurrent.Await
    import scala.concurrent.duration.Duration
    Await.result(system.whenTerminated, Duration.Inf)

    // Cheery outro to the Hangman game.
    println("\nThank you for playing Akka Hangman!")
  }
}
