# Akka Hangman #

----------
The Hangman word-guessing game hits a nice sweet spot when learning a new computer language. Not as trivial as "Hello World" but not overly difficult to implement.

This version of Hangman was written utilizing the [Scala](https://www.scala-lang.org/index.html "https://www.scala-lang.org/index.html") language, version 2.12.8 and the [Akka](https://akka.io) Actor library, version 2.5.21.

The original *master* branch utilizes the ```"akka-actor"``` untyped library.  Recently, the project was migrated over to the ```"akka-actor-typed"```  typed library and is stored in the *typed* branch.

In this project, [SBT or *The simple Scala build tool* ](http://www.scala-sbt.org/ "http://www.scala-sbt.org/") was utilized to compile Hangman. To build Hangman, use this command:

    sbt.bat compile

To run Hangman, invoke the following command:

    sbt.bat run

Important: Use SBT 1.0.2 or later.

## Deployment

The Akka Hangman `plugins.sbt` file contains the [SBT Native Packager](https://github.com/sbt/sbt-native-packager).  This is the recommended route for generating a distributable package.  From the project root folder, issue the command:

```
sbt.bat universal:packageBin
```

From the project root folder, go to the `target\universal ` directory.  If all went well, you should find a zip file, such as:

```
akka-hangman-1.0.0.zip
```

This is the server distributable package.  Unpack (unzip) to a select folder.  Then, on Windows, run

```
akka-hangman.bat
```

The Akka Hangman program is text based as shown by:

![console view](https://github.com/ROpsal/akka-hangman/blob/master/images/console.png)