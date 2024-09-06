package carcassonne.util

import scalafx.scene.effect.ColorAdjust
import scalafx.scene.paint.Color as FXColor

enum Color(val colorHue: Double, val colorBrightness: Double, val fxColor: FXColor):
  case Black extends Color(0, -1, FXColor.Black)
  case Red extends Color(0, 0, FXColor.Red)
  case Yellow extends Color(60, 0, FXColor.Yellow)
  case Green extends Color(120, 0, FXColor.Green)
  case Blue extends Color(240, 0, FXColor.Blue)
  case Purple extends Color(300, 0, FXColor.Purple)

  def getColorAdjust: ColorAdjust =
    val adjustedHue = Color.rangeCalculator((colorHue + 180) % 360, 0, 360, -1, 1)
    new ColorAdjust():
      hue = adjustedHue
      brightness = colorBrightness
      saturation = 1.0
      contrast = 0.0

  def getSFXColor: FXColor = fxColor

object Color:
  private def rangeCalculator(value: Double, start: Double, stop: Double, targetStart: Double, targetStop: Double): Double =
    targetStart + (targetStop - targetStart) * ((value - start) / (stop - start))

object PlayerColor:
  private val availableColors = List(Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Purple)

  def assignColors(playerNames: List[String]): List[(String, Color)] =
    playerNames.zipWithIndex.map { case (name, index) =>
      (name, availableColors(index % availableColors.size))
    }