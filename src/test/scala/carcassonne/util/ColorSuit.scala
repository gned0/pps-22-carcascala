package carcassonne.util

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class ColorSuit extends AnyFunSuite with Matchers{
  test("Colors should be assigned in the correct order"){
    val playerNames = List("Alice", "Bob", "Charlie", "Dave", "Eve")
    val playersWithColors = PlayerColor.assignColors(playerNames)

    playersWithColors should contain theSameElementsAs List(
      ("Alice", Color.Red),
      ("Bob", Color.Blue),
      ("Charlie", Color.Green),
      ("Dave", Color.Yellow),
      ("Eve", Color.Purple),
    )
  }

}
