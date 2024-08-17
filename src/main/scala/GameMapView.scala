
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
import scalafx.geometry.Pos.Center

class GameMapView extends GridPane with Observer[GameMap] with Subject[GameMapView] {
  private val mapSize = 5 // 5x5 grid for simplicity
  private var _lastPosition: Position = Position(0, 0)
  private var _tilesPlaced: Map[Position, Region] = Map()

  this.alignment = Center

  val x = 2
  val y = 2

  val placeholderTile = new Region {
    prefWidth = 100
    prefHeight = 100
    styleClass += "placeholderTile"
  }
  add(placeholderTile, x, y) // Add the button to the grid

  // Region action when clicked
  placeholderTile.onMouseClicked = _ => tileClicked(Position(x, y), placeholderTile)


  def lastTilePlacedPosition: Option[Position] = Option.apply(_lastPosition)
  def tileClicked(position: Position, placedTile: Region): Unit =
    if _tilesPlaced.isEmpty then
      _lastPosition = position
      _tilesPlaced = _tilesPlaced + (position -> placedTile)
      notifyTileClick(position)
    else
      for (positionCollected <- _tilesPlaced.keys) do
        positionCollected match
          case p if p == position =>
            log("Tile already placed there")
          case Position(_, _) =>
            _lastPosition = position
            _tilesPlaced = _tilesPlaced + (position -> placedTile)
            notifyTileClick(position)


  def notifyTileClick(position: Position): Unit =
    log(s"Tile placed at $position")
    notifyObservers()

  override def receiveUpdate(subject: GameMap): Unit =
    log("Model Updated")


  def log(string: String): Unit =
    print(s"VIEW - " + string + "\n")

}
