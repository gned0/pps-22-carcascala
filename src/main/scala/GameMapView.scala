
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
import scalafx.scene.layout.GridPane.{getColumnIndex, getRowIndex}

class GameMapView extends GridPane with Observer[GameMap] with Subject[GameMapView] {
  private val mapSize = 5 // 5x5 grid for simplicity
  private var _lastPositionTilePlaced: Position = Position(0, 0)
  private var _tilesPlaced: Map[Position, Region] = Map()

  this.alignment = Center

  val x = 100
  val y = 100

  val placeholderTile = new Region {
    prefWidth = 100
    prefHeight = 100
    styleClass += "placeholderTile"
  }
  add(placeholderTile, x, y) // Add the button to the grid

  // Region action when clicked
  placeholderTile.onMouseClicked = _ => tileClicked(Position(x, y), placeholderTile)


  def tileClicked(position: Position, placedTile: Region): Unit =
    if _tilesPlaced.isEmpty then
      _lastPositionTilePlaced = position
      placedTile.getStyleClass.remove(0)
      placedTile.setOnMouseClicked(null)
      placedTile.styleClass += "placedTile"
      _tilesPlaced = _tilesPlaced + (position -> placedTile)
      this.getChildren.removeIf(node => getColumnIndex(node) == _lastPositionTilePlaced.x && getRowIndex(node) == _lastPositionTilePlaced.y)
      this.add(placedTile, position.x, position.y)
      notifyTileClick(position)
      createNewPlaceholders()
    else
      for (positionCollected <- _tilesPlaced.keys) do
        positionCollected match
          case p if p == position =>
            log("Tile already placed there")
          case Position(_, _) =>
            _lastPositionTilePlaced = position
            placedTile.getStyleClass.remove(0)
            placedTile.setOnMouseClicked(null)
            placedTile.styleClass += "placedTile"
            _tilesPlaced = _tilesPlaced + (position -> placedTile)
            this.getChildren.removeIf(node => getColumnIndex(node) == _lastPositionTilePlaced.x && getRowIndex(node) == _lastPositionTilePlaced.y)
            this.add(placedTile, position.x, position.y)
            notifyTileClick(position)
            createNewPlaceholders()


  def createNewPlaceholders(): Unit =
    for (posX <- Seq(_lastPositionTilePlaced.x - 1, _lastPositionTilePlaced.x + 1)) do
      if !_tilesPlaced.contains(Position(posX, _lastPositionTilePlaced.y)) then
        val placeholderTile = new Region {
          prefWidth = 100
          prefHeight = 100
          styleClass += "placeholderTile"
        }
        val tmp: Position = Position(posX, _lastPositionTilePlaced.y)
        println(tmp)
        add(placeholderTile, tmp.x, tmp.y) // Add the button to the grid
        placeholderTile.onMouseClicked = _ => tileClicked(tmp, placeholderTile)

    for (posY <- Seq(_lastPositionTilePlaced.y - 1, _lastPositionTilePlaced.y + 1)) do
      if !_tilesPlaced.contains(Position(_lastPositionTilePlaced.x, posY)) then
        val placeholderTile = new Region {
          prefWidth = 100
          prefHeight = 100
          styleClass += "placeholderTile"
        }
        val tmp: Position = Position(_lastPositionTilePlaced.x, posY)
        add(placeholderTile, tmp.x, tmp.y) // Add the button to the grid
        placeholderTile.onMouseClicked = _ => tileClicked(tmp, placeholderTile)


  //      posX match
//        case Position(posX, _lastPositionTilePlaced.y) == _lastPositionTilePlaced =>
//          log("No need to place a placeholder")
//        case

  def notifyTileClick(position: Position): Unit =
    log(s"Tile placed at $position")
    notifyObservers()

  override def receiveUpdate(subject: GameMap): Unit =
    log("Model Updated")


  def lastTilePlacedPosition: Option[Position] = Option.apply(_lastPositionTilePlaced)
  def log(string: String): Unit =
    print(s"VIEW - " + string + "\n")

}
