package carcassonne.util

import scalafx.scene.effect.ColorAdjust
import scalafx.scene.paint.Color as FXColor

/**
 * Enumeration representing different colors used in the game.
 *
 * @param colorHue The hue of the color.
 * @param colorBrightness The brightness of the color.
 * @param fxColor The corresponding JavaFX color.
 */
enum Color(val colorHue: Double, val colorBrightness: Double, val fxColor: FXColor):
  /**
   * Black color with hue 0 and brightness -1.
   */
  case Black extends Color(0, -1, FXColor.Black)

  /**
   * Red color with hue 0 and brightness 0.
   */
  case Red extends Color(0, 0, FXColor.Red)

  /**
   * Yellow color with hue 60 and brightness 0.
   */
  case Yellow extends Color(60, 0, FXColor.Yellow)

  /**
   * Green color with hue 120 and brightness 0.
   */
  case Green extends Color(120, 0, FXColor.Green)

  /**
   * Blue color with hue 240 and brightness 0.
   */
  case Blue extends Color(240, 0, FXColor.Blue)

  /**
   * Purple color with hue 300 and brightness 0.
   */
  case Purple extends Color(300, 0, FXColor.Purple)

  /**
   * Creates a ColorAdjust effect based on the color's hue and brightness.
   *
   * @return A ColorAdjust effect with adjusted hue and brightness.
   */
  def getColorAdjust: ColorAdjust =
    val adjustedHue = Color.rangeCalculator((colorHue + 180) % 360, 0, 360, -1, 1)
    new ColorAdjust:
      hue = adjustedHue
      brightness = colorBrightness
      saturation = 1.0
      contrast = 0.0

  /**
   * Retrieves the JavaFX color associated with this color.
   *
   * @return The JavaFX color.
   */
  def getSFXColor: FXColor = fxColor

object Color:
  /**
   * Calculates a value within a target range based on its position within a source range.
   *
   * @param value The value to be recalculated.
   * @param start The start of the source range.
   * @param stop The end of the source range.
   * @param targetStart The start of the target range.
   * @param targetStop The end of the target range.
   * @return The recalculated value within the target range.
   */
  private def rangeCalculator(value: Double, start: Double, stop: Double, targetStart: Double, targetStop: Double): Double =
    targetStart + (targetStop - targetStart) * ((value - start) / (stop - start))

  /**
   * Creates a custom JavaFX color based on RGB and alpha values.
   *
   * @param r The red component (0-255).
   * @param g The green component (0-255).
   * @param b The blue component (0-255).
   * @param alpha The alpha component (0.0-1.0).
   * @return The custom JavaFX color.
   */
  def getCustomSFXColor(r: Double, g: Double, b: Double, alpha: Double): FXColor =
    FXColor(
      rangeCalculator(r, 0, 255, 0, 1),
      rangeCalculator(g, 0, 255, 0, 1),
      rangeCalculator(b, 0, 255, 0, 1),
      alpha
    )

/**
 * Object responsible for assigning colors to players.
 */
object PlayerColor:
  private val availableColors = List(Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Purple)

  /**
   * Assigns colors to a list of player names in a round-robin fashion.
   *
   * @param playerNames The list of player names.
   * @return A list of tuples containing player names and their assigned colors.
   */
  def assignColors(playerNames: List[String]): List[(String, Color)] =
    playerNames.zipWithIndex.map { case (name, index) =>
      (name, availableColors(index % availableColors.size))
    }