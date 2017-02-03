/**
  * Created by Richard on 2/3/2017.
  */

import org.scalatest.{ Suite, BeforeAndAfterAll }
import akka.testkit.TestKit


trait StopSystemAfterAll extends BeforeAndAfterAll {

  this: TestKit with Suite =>
  override protected def afterAll(): Unit = {
    super.afterAll()
    system.terminate()
  }

}
