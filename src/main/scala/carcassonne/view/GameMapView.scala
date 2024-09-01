package carcassonne.view

import carcassonne.model.game.{GameMatch, Player}
import carcassonne.model.tile.{GameTile, GameTileFactory, TileSegment}
import carcassonne.observers.{ObserverGameMatch, SubjectGameView}
import carcassonne.util.{Logger, Position}
import javafx.scene.layout.GridPane.{getColumnIndex, getRowIndex}
import scalafx.geometry.Pos
import scalafx.scene.input.{MouseButton, MouseEvent}
import scalafx.scene.layout.{GridPane, HBox, Region, VBox}
import scalafx.scene.text.Text
import scalafx.Includes.*
import scalafx.event.EventIncludes.eventClosureWrapperWithParam
import scalafx.scene.control.Button
import scalafx.scene.image.{Image, ImageView}


/**
 * The view for the game map.
 * This class extends `GridPane` and implements `SubjectGameView` and `ObserverGameMap`.
 */
class GameMapView(onSwitchToStarterView: () => Unit) extends GridPane
  with SubjectGameView[GameMapView] with ObserverGameMatch[GameMatch]:

  private val mapSize = 5 // 5x5 grid for simplicity
  private var _lastTilePlaced: Region = new Region()

  private var _drawnTile: GameTile = GameTileFactory.createStartTile()
  private var _drawnTileImage: ImageView = ImageView(new Image(getClass.getResource("../../tiles/" + GameTileFactory.createStartTile().imagePath).toExternalForm))

  private val rotateClockwise = Button("Clockwise")
  rotateClockwise.onMouseClicked = _ => rotateDrawnTileClockwise()

  private val rotateCounterClockwise = Button("Counter Clockwise")
  rotateCounterClockwise.onMouseClicked = _ => rotateDrawnTileCounterClockwise()

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
    val placedGameTile = tiles(position)

    placedTile.styleClass.clear()
    placedTile.onMouseClicked = null
    // Remove the old placeholder
    this.getChildren.removeIf(node =>
      getColumnIndex(node) == position.x && getRowIndex(node) == position.y
    )
    // Replace the tile that has been just removed with new attributes
    this.add(_drawnTileImage, position.x, position.y)

  /**
   * Creates new placeholder tiles around the last placed tile.
   * @param tiles the current state of the game map tiles
   */
  def createNewPlaceholders(tiles: Map[Position, GameTile], position: Position): Unit =
    for
      posX <- Seq(position.x - 1, position.x + 1)
      if !tiles.contains(Position(posX, position.y))
    do
      val placeholderTile = createPlaceholderTile(Position(posX, position.y))

    for
      posY <- Seq(position.y - 1, position.y + 1)
      if !tiles.contains(Position(position.x, posY))
    do
      val placeholderTile = createPlaceholderTile(Position(position.x, posY))

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

  def rotateDrawnTileClockwise(): Unit =
    _drawnTile = _drawnTile.rotateClockwise
    _drawnTileImage.rotate = _drawnTileImage.getRotate + 90
    println(_drawnTile)
    Logger.log(s"VIEW", "Drawn tile rotated clockwise")

  def rotateDrawnTileCounterClockwise(): Unit =
    _drawnTile = _drawnTile.rotateCounterClockwise
    _drawnTileImage.rotate = _drawnTileImage.getRotate - 90
    println(_drawnTile)
    Logger.log(s"VIEW", "Drawn tile rotated counter clockwise")
  
  /**
   * Returns the last placed tile.
   * @return the last placed tile
   */
  def getLastTilePlaced: Option[Region] = Some(_lastTilePlaced)

  def getDrawnTilePane: Option[GridPane] = Some(drawnTilePane)

  override def tileDrawn(tileDrawn: GameTile): Unit =
    _drawnTile = tileDrawn
    drawnTilePane.getChildren.clear()
    _drawnTileImage = new ImageView(new Image(getClass.getResource("../../tiles/" + tileDrawn.imagePath).toExternalForm))
    _drawnTileImage.fitWidth = 100
    _drawnTileImage.fitHeight = 100
    _drawnTileImage.preserveRatio = true

    drawnTilePane.add(new Text(s"North Border: \n${tileDrawn.segments(TileSegment.N)}"), 10, 10)
    drawnTilePane.add(new Text(s"East Border: \n${tileDrawn.segments(TileSegment.E)}"), 11, 11)
    drawnTilePane.add(new Text(s"South Border: \n${tileDrawn.segments(TileSegment.S)}"), 10, 12)
    drawnTilePane.add(new Text(s"West Border: \n${tileDrawn.segments(TileSegment.W)}"), 9, 11)
    drawnTilePane.add(_drawnTileImage, 10, 11)

  def addDrawnTilePane(): Unit =
    val menuColumn = new VBox {
      prefWidth = 250 // Fixed width for the menu column
      style = "-fx-background-color: darkgray;" // Background color for the menu column

      // Add some example buttons to the menu
      children = Seq(
        drawnTilePane,
        new HBox {
          children = Seq(
            rotateClockwise,
            rotateCounterClockwise
          )
        }
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
        createNewPlaceholders(tiles, position)
      else
        placeTile(position, getLastTilePlaced.get, tiles)
        createNewPlaceholders(tiles, position)

  override def gameEnded(players: List[Player]): Unit =
    GameEndView(players).popupStage.show()
    onSwitchToStarterView()