package mainApplication

import observers.{ObserverGameMap, SubjectGameView}
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.{GridPane, Region}
import scalafx.geometry.{Insets, Pos}
import scalafx.Includes.*
import scalafx.scene.input.{MouseButton, MouseEvent}
import scalafx.scene.paint.Color.{Black, Grey}
import scalafx.scene.layout.GridPane.{getColumnIndex, getRowIndex}

class GameMapView extends GridPane with SubjectGameView[GameMapView] with ObserverGameMap[GameMap]:
  private val mapSize = 5 // 5x5 grid for simplicity
  private var _lastPositionTilePlaced: Position = Position(0, 0)
  private var _lastTilePlaced: Region = new Region()

  this.alignment = Pos.Center

  // TODO Temporary, rethink about optimal initial coordinates
  val x = 100
  val y = 100

  createPlaceholderTile(Position(x, y))

  def placeTile(position: Position, placedTile: Region, tiles: Map[Position, GameTile]): Unit =
    _lastPositionTilePlaced = position
    placedTile.styleClass.clear()
    placedTile.styleClass += "placedTile"
    placedTile.onMouseClicked = null
    // Remove the old placeholder
    this.getChildren.removeIf(node =>
      getColumnIndex(node) == position.x && getRowIndex(node) == position.y
    )
    // Replace the tile that has been just removed with new attributes
    this.add(placedTile, position.x, position.y)

  def createNewPlaceholders(tiles: Map[Position, GameTile]): Unit =
    for
      posX <- Seq(_lastPositionTilePlaced.x - 1, _lastPositionTilePlaced.x + 1)
      if !tiles.contains(Position(posX, _lastPositionTilePlaced.y))
    do
      val placeholderTile = createPlaceholderTile(Position(posX, _lastPositionTilePlaced.y))

    for
      posY <- Seq(_lastPositionTilePlaced.y - 1, _lastPositionTilePlaced.y + 1)
      if !tiles.contains(Position(_lastPositionTilePlaced.x, posY))
    do
      val placeholderTile = createPlaceholderTile(Position(_lastPositionTilePlaced.x, posY))

  def createPlaceholderTile(position: Position): Region =
    new Region:
      prefWidth = 100
      prefHeight = 100
      styleClass += "placeholderTile"
      onMouseClicked = (event: MouseEvent) => if event.button == MouseButton.Primary then
        checkClickedTile(position, this)
      add(this, position.x, position.y)

  def checkClickedTile(position: Position, placedTile: Region): Unit =
    _lastTilePlaced = placedTile
    notifyTilePlacementAttempt(position)

  def getLastTilePlacedPosition: Option[Position] = Some(_lastPositionTilePlaced)
  def getLastTilePlaced: Option[Region] = Some(_lastTilePlaced)

  override def isTilePlaced(isTilePlaced: Boolean,
                            tilesOption: Option[Map[Position, GameTile]],
                            position: Position): Unit =
    println(isTilePlaced)
    val tiles = tilesOption.get
    if isTilePlaced then
      if tiles.isEmpty then
        placeTile(position, getLastTilePlaced.get, tiles)
        createNewPlaceholders(tiles)
      else
        placeTile(position, getLastTilePlaced.get, tiles)
        createNewPlaceholders(tiles)

    println(tiles)

  def log(string: String): Unit =
    println(s"VIEW - $string")