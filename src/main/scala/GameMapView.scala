
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.{Border, BorderStroke, GridPane, Region}
import scalafx.scene.control.Button
import scalafx.geometry.Insets
import scalafx.Includes.*
import scalafx.scene.paint.CycleMethod.NoCycle
import scalafx.scene.paint.{Color, LinearGradient, Stops}
import scalafx.scene.shape.Rectangle
import scalafx.scene.Cursor.Hand
import scalafx.scene.paint.Color.{Black, Grey}
import scalafx.scene.layout.BorderStrokeStyle.Dashed
import scalafx.scene.layout.CornerRadii.Empty
import scalafx.scene.layout.BorderWidths.Default

class GameMapView extends GridPane {
  private val mapSize = 5 // 5x5 grid for simplicity
  private var _lastPosition = Position(0, 0)

  // Create a 5x5 grid of buttons
  for x <- 0 until mapSize do
    for y <- 0 until mapSize do
      val placeholderTile = new Region {
        prefWidth = 100
        prefHeight = 100
        styleClass += "placeholderTile"
      }
      add(placeholderTile, x, y) // Add the button to the grid

      // Region action when clicked
      placeholderTile.onMouseClicked = _ => notifyTileClick(Position(x, y))

  def lastTilePlacedPosition: Position = _lastPosition
  def notifyTileClick(position: Position): Unit =
  // Placeholder for controller to handle tile placement
    log(s"Tile placed at $position")
    _lastPosition = position
    log("x: " + _lastPosition.x + " - y:" + _lastPosition.y)

  def log(string: String): Unit =
    print(s"VIEW - " + string + "\n")

}
