package carcassonne.util

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import scalafx.scene.effect.ColorAdjust
import scalafx.scene.paint.Color as FXColor

class ColorSuit extends AnyFunSuite with Matchers {
  test("Colors should be assigned in the correct order") {
    val playerNames = List("Alice", "Bob", "Charlie", "Dave", "Eve")
    val playersWithColors = PlayerColor.assignColors(playerNames)

    playersWithColors should contain theSameElementsAs List(
      ("Alice", Color.Red),
      ("Bob", Color.Blue),
      ("Charlie", Color.Green),
      ("Dave", Color.Yellow),
      ("Eve", Color.Purple)
    )
  }

  test("getColorAdjust should return correct ColorAdjust") {
    val blackAdjust = Color.Black.getColorAdjust
    blackAdjust.hue.value should be(0.0) // 0 hue for Black
    blackAdjust.brightness.value should be(-1.0) // -1 brightness for Black
    blackAdjust.saturation.value should be(1.0)
    blackAdjust.contrast.value should be(0.0)

    val redAdjust = Color.Red.getColorAdjust
    redAdjust.hue.value should be(0.0)
    redAdjust.brightness.value should be(0.0)
    redAdjust.saturation.value should be(1.0)
    redAdjust.contrast.value should be(0.0)
  }

  test("getSFXColor should return the correct FXColor for each enum value") {
    Color.Black.getSFXColor should be(FXColor.Black)
    Color.Red.getSFXColor should be(FXColor.Red)
    Color.Yellow.getSFXColor should be(FXColor.Yellow)
    Color.Green.getSFXColor should be(FXColor.Green)
    Color.Blue.getSFXColor should be(FXColor.Blue)
    Color.Purple.getSFXColor should be(FXColor.Purple)
  }

}
