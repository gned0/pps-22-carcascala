package carcassonne.view

import carcassonne.model.{GameMatch, GameTile, Position}
import carcassonne.observers.{ObserverGameMatch, SubjectGameView}
import javafx.scene.layout.GridPane.{getColumnIndex, getRowIndex}
import scalafx.geometry.Pos
import scalafx.scene.input.{MouseButton, MouseEvent}
import scalafx.scene.layout.{GridPane, Region, VBox}
import scalafx.scene.text.Text
import scalafx.Includes.*
import scalafx.event.EventIncludes.eventClosureWrapperWithParam
import scalafx.scene.image.{Image, ImageView}


/**
 * The view for the game map.
 * This class extends `GridPane` and implements `SubjectGameView` and `ObserverGameMap`.
 */
class GameMapView extends GridPane with SubjectGameView[GameMapView] with ObserverGameMatch[GameMatch]:

  private val mapSize = 5 // 5x5 grid for simplicity
  private var _lastPositionTilePlaced: Position = Position(0, 0)
  private var _lastTilePlaced: Region = new Region()
  private var _drawnTile: GameTile = GameTile.startTile

  private val drawnTilePane = GridPane()
  drawnTilePane.alignment = Pos.CenterRight
  drawnTilePane.mouseTransparent = true

  this.prefWidth = 600
  this.prefHeight = 400

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
    val placedGameTile = tiles(position)
    val imageView = new ImageView(new Image(getClass.getResource("../../tiles/" + placedGameTile.imgPath).toExternalForm))
    imageView.fitWidth = 100
    imageView.fitHeight = 100
    imageView.preserveRatio = true

    placedTile.styleClass.clear()
    placedTile.onMouseClicked = null
    // Remove the old placeholder
    this.getChildren.removeIf(node =>
      getColumnIndex(node) == position.x && getRowIndex(node) == position.y
    )
    // Replace the tile that has been just removed with new attributes
    this.add(imageView, position.x, position.y)

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
    notifyTilePlacementAttempt(_drawnTile, position)

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

  def getDrawnTilePane: Option[GridPane] = Some(drawnTilePane)

  override def tileDrawn(tileDrawn: GameTile): Unit =
    _drawnTile = tileDrawn
    drawnTilePane.getChildren.clear()
    val imageView = new ImageView(new Image(getClass.getResource("../../tiles/" + tileDrawn.imgPath).toExternalForm))
    imageView.fitWidth = 100
    imageView.fitHeight = 100
    imageView.preserveRatio = true

    drawnTilePane.add(new Text(s"North Border: \n${tileDrawn.north}"), 10, 10)
    drawnTilePane.add(new Text(s"East Border: \n${tileDrawn.east}"), 11, 11)
    drawnTilePane.add(new Text(s"South Border: \n${tileDrawn.south}"), 10, 12)
    drawnTilePane.add(new Text(s"West Border: \n${tileDrawn.west}"), 9, 11)
    drawnTilePane.add(imageView, 10, 11)

  def addDrawnTilePane(): Unit =
    val menuColumn = new VBox {
      prefWidth = 250 // Fixed width for the menu column
      style = "-fx-background-color: darkgray;" // Background color for the menu column

      // Add some example buttons to the menu
      children = Seq(
        drawnTilePane
      )
    }
    menuColumn.alignment = Pos.TopLeft
    this.getScene.getChildren.add(menuColumn)

  /**
   * Called when a tile is placed on the game map.
   *
   * @param isTilePlaced whether the tile was successfully placed
   * @param tilesOption the current state of the game map tiles
   * @param position the position where the tile was placed
   */
  override def isTilePlaced(isTilePlaced: Boolean,
                            tilesOption: Option[Map[Position, GameTile]],
                            position: Position): Unit =
    val tiles = tilesOption.get
    if isTilePlaced then
      if tiles.isEmpty then
        placeTile(position, getLastTilePlaced.get, tiles)
        createNewPlaceholders(tiles)
      else
        placeTile(position, getLastTilePlaced.get, tiles)
        createNewPlaceholders(tiles)