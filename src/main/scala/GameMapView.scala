
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.GridPane
import scalafx.scene.control.Button
import scalafx.geometry.Insets
import scalafx.Includes._
class GameMapView extends GridPane {
  private val mapSize = 5 // 5x5 grid for simplicity
  private var _lastPosition = Position(0, 0)

  // Create a 5x5 grid of buttons
  for x <- 0 until mapSize do
    for y <- 0 until mapSize do
      val btn = new Button {
        text = s"($x, $y)"
        prefWidth = 100
        prefHeight = 100
      }

      add(btn, x, y) // Add the button to the grid
      // Button action when clicked
      btn.onAction = _ => notifyTileClick(Position(x, y))

  def lastTilePlacedPosition = _lastPosition
  def notifyTileClick(position: Position): Unit =
  // Placeholder for controller to handle tile placement
    log(s"Tile placed at $position")
    _lastPosition = position
    log("x: " + _lastPosition.x + " - y:" + _lastPosition.y)

  def log(string: String): Unit =
    print(s"VIEW - " + string + "\n")

}
