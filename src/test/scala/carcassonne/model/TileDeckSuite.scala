package carcassonne.model

import carcassonne.model.tile.TileDeck
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import scala.util.Try

class TileDeckSuite extends AnyFunSuite with Matchers {

  test("TileDeck should initialize with tiles from the default config file") {
    val deck = TileDeck()
    deck.draw() should not be None
  }

  test("TileDeck should return None when drawing from an empty deck") {
    val deck = TileDeck()

    // Draw all tiles
    while (deck.draw().isDefined) {}

    deck.draw() shouldBe None
  }

  test("TileDeck should draw all tiles in the correct order") {
    val deck = TileDeck()
    val tiles = Iterator.continually(deck.draw()).takeWhile(_.isDefined).map(_.get).toList

    tiles should not be empty 
  }

  test("TileDeck should shuffle tiles") {
    val deck1 = TileDeck()
    val deck2 = TileDeck()

    val tiles1 = Iterator.continually(deck1.draw()).takeWhile(_.isDefined).map(_.get).toList
    val tiles2 = Iterator.continually(deck2.draw()).takeWhile(_.isDefined).map(_.get).toList

    tiles1 should not be tiles2 
  }

  test("TileDeck should throw an exception if the config file does not exist") {
    val nonExistentConfigFile = "non-existent-file.json"
    an [java.lang.NullPointerException] should be thrownBy TileDeck(nonExistentConfigFile)
  }
}
