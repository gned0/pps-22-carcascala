package carcassonne.model

import carcassonne.model.TileDeck
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class TileDeckSuite extends AnyFunSuite with Matchers {

  test("TileDeck should initialize with tiles from the default config file") {
    val deck = new TileDeck()
    deck.isEmpty shouldBe false
  }

  test("TileDeck should allow drawing a tile and reduce the number of tiles") {
    val deck = new TileDeck()
    val initialSize = Try(deck.draw()).map(_ => deck.getSize).getOrElse(0)
    val tileOpt = deck.draw()

    tileOpt should not be None
    deck.getSize shouldBe initialSize - 1
  }

  test("TileDeck should return None when drawing from an empty deck") {
    val deck = new TileDeck()

    // Draw all tiles
    while (deck.draw().isDefined) {}

    deck.isEmpty shouldBe true
    deck.draw() shouldBe None
  }
  
}
