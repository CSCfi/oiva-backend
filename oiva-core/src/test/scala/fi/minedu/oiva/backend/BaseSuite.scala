package fi.minedu.oiva.backend

import org.scalatest.FunSuite
import org.scalatest.mock.MockitoSugar

import scala.io.Source

abstract class BaseSuite extends FunSuite with MockitoSugar {

  def readResource(filename: String): String = {
    val source = Source.fromURL(getClass.getResource(filename))
    try source.mkString finally source.close()
  }

}
