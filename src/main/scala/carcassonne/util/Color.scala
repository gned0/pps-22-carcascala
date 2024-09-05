package carcassonne.util

import scalafx.scene.paint.Color as FXColor

enum Color:
  case Black, Red, Yellow, Green, Blue, Purple

object PlayerColor: 

  // Define a list of available colors
  private val availableColors = List(Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Purple)

  /**
   * Assigns unique colors to a list of player names.
   *
   * @param playerNames A list of player names.
   * @return A list of tuples, where each tuple contains a player name and a color.
   */
  def assignColors(playerNames: List[String]): List[(String, Color)] = {
    playerNames.zipWithIndex.map {
      case (name, index) =>
        val color = availableColors(index % availableColors.size)
        (name, color)
    }
  }

  def colorAdjustCalculator(color: Color): (Double, Double) =
    def rangeCalculator(value: Double, start: Double, stop: Double, targetStart: Double, targetStop: Double): Double =
      targetStart + (targetStop - targetStart) * ((value - start) / (stop - start))

    var hue = 0
    var brightness = 0
    color match
      case Color.Black => brightness = -1
      case Color.Red => hue = 0
      case Color.Yellow => hue = 60
      case Color.Green => hue = 120
      case Color.Blue => hue = 240
      case Color.Purple => hue = 300

    (rangeCalculator((hue + 180) % 360, 0, 360, -1, 1), brightness)

  def getNormalColor(color: Color): FXColor =
    color match
      case Color.Black => FXColor.Black
      case Color.Red => FXColor.Red
      case Color.Yellow => FXColor.Yellow
      case Color.Green => FXColor.Green
      case Color.Blue => FXColor.Blue
      case Color.Purple => FXColor.Purple


