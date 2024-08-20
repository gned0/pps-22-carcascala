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

/**
 * The view for the game map.
 * This class extends `GridPane` and implements `SubjectGameView` and `ObserverGameMap`.
 */
class GameMapView extends GridPane with SubjectGameView[GameMapView] with ObserverGameMap[GameMap]:

  private val mapSize = 5 // 5x5 grid for simplicity
  private var _lastPositionTilePlaced: Position = Position(0, 0)
  private var _lastTilePlaced: Region = new Region()

  this.alignment = Pos.Center

  // Initial coordinates for the placeholder tile
  val x = 100
  val y = 100

  createPlaceholderTile(Position(x, y))

  /**
   * Places a tile at the specified position in the view.
   * @param position the position where the tile should be placed
   * @param placedTile the tile to place
   * @param tiles the current state of the game map tiles
   */
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

  /**
   * Creates new placeholder tiles around the last placed tile.
   * @param tiles the current state of the game map tiles
   */
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

  /**
   * Creates a placeholder tile at the specified position.
   * @param position the position where the placeholder tile should be created
   * @return the created placeholder tile
   */
  def createPlaceholderTile(position: Position): Region =
    new Region:
      prefWidth = 100
      prefHeight = 100
      styleClass += "placeholderTile"
      onMouseClicked = (event: MouseEvent) => if event.button == MouseButton.Primary then
        checkClickedTile(position, this)
      add(this, position.x, position.y)

  /**
   * Checks the clicked tile and notifies observers of a tile placement attempt.
   * @param position the position of the clicked tile
   * @param placedTile the clicked tile
   */
  def checkClickedTile(position: Position, placedTile: Region): Unit =
    _lastTilePlaced = placedTile
    notifyTilePlacementAttempt(position)

  /**
   * Returns the position of the last placed tile.
   * @return the position of the last placed tile
   */
  def getLastTilePlacedPosition: Option[Position] = Some(_lastPositionTilePlaced)

  /**
   * Returns the last placed tile.
   * @return the last placed tile
   */
  def getLastTilePlaced: Option[Region] = Some(_lastTilePlaced)

  /**
   * Called when a tile is placed on the game map.
   * @param isTilePlaced whether the tile was successfully placed
   * @param tilesOption the current state of the game map tiles
   * @param position the position where the tile was placed
   */
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

  /**
   * Logs a message to the console.
   * @param string the message to log
   */
  def log(string: String): Unit =
    println(s"VIEW - $string")