package carcassonne.view.gameMatch

import carcassonne.model.game.{GameState, Player}
import carcassonne.model.tile.TileSegment.N
import carcassonne.model.tile.{GameTile, TileSegment}
import carcassonne.observers.observers.model.ObserverGameMatchBoard
import carcassonne.observers.observers.view.ObserverGameMenuView
import carcassonne.observers.subjects.view.{SubjectGameMatchView, SubjectGameMenuView, SubjectStarterView}
import carcassonne.util.{Logger, Position}
import carcassonne.view.gameEnd.GameEndView
import javafx.scene.layout.GridPane.{getColumnIndex, getRowIndex}
import javafx.scene.layout.Region.positionInArea
import scalafx.Includes.*
import scalafx.event.EventIncludes.eventClosureWrapperWithParam
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Node
import scalafx.scene.control.Button
import scalafx.scene.effect.ColorAdjust
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.{MouseButton, MouseEvent}
import scalafx.scene.layout.*
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.Text


/**
 * The view for the game map.
 * This class extends `GridPane` and implements `SubjectGameView` and `ObserverGameMap`.
 */
class GameMatchBoardView(gameEndedSwitchView: () => Unit) extends GridPane
  with ObserverGameMenuView
  with SubjectGameMatchView
  with ObserverGameMatchBoard:

  this.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE)
  this.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE)
  this.setMaxSize(Double.MaxValue, Double.MaxValue)
  GridPane.setHgrow(this, Priority.Always)
  GridPane.setVgrow(this, Priority.Always)

  this.prefWidth = 600
  this.prefHeight = 400

  this.alignment = Pos.Center

  /**
   * Places a tile at the specified position in the view.
 *
   * @param position the position where the tile should be placed
   * @param tiles the current state of the game map tiles
   */
  private def placeTile(position: Position, tileGraphicElement: Node): Unit =
    // Remove the old placeholder
    this.getChildren.removeIf(node =>
      getColumnIndex(node) == position.x && getRowIndex(node) == position.y
    )
    this.add(tileGraphicElement, position.x, position.y)

  /**
   * Creates new placeholder tiles around the last placed tile.
   * @param tiles the current state of the game map tiles
   */
  private def createNewPlaceholders(tiles: Map[Position, GameTile], position: Position): Unit =
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
  private def createPlaceholderTile(position: Position): Region =
    new Region:
      prefWidth = 100
      prefHeight = 100
      styleClass += "placeholderTile"
      onMouseClicked = (event: MouseEvent) => if event.button == MouseButton.Primary then
        notifyTilePlacementAttempt(getDrawnTile._1, position)
      add(this, position.x, position.y)


  private def removeFollowerGridPane(position: Position): Unit =
    val graphicalTile: Option[javafx.scene.Node] = this.getChildrenUnmodifiable.toArray.find {
      case child: javafx.scene.Node => GridPane.getColumnIndex(child) == position.x && GridPane.getRowIndex(child) == position.y
    }.map(_.asInstanceOf[javafx.scene.Node])

    graphicalTile match
      case Some(s) if s.isInstanceOf[javafx.scene.layout.StackPane] =>
        val stackPane = s.asInstanceOf[javafx.scene.layout.StackPane]
        stackPane.getChildren.remove(1)
      case Some(_) => println("Other type of element")
      case None => println("No element")

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
        placeTile(position, getDrawnTile._2)
        createNewPlaceholders(tiles, position)

  override def gameEnded(players: List[Player]): Unit =
    GameEndView(players).popupStage.show()
    gameEndedSwitchView()

  override def isFollowerPlaced(position: Position, segment: TileSegment, player: Player): Unit = ()

  override def playerChanged(player: Player): Unit =
    setCurrentPlayer(player)

  override def availableFollowerPositions(availSegments: List[TileSegment], position: Position): Unit =
    placeTile(position, 
      GameMatchFollowerGridView(
        availSegments, 
        getDrawnTile._2,
        getCurrentPlayer,
        position,
        notifyFollowerPlacement
      )
    )

  override def scoreCalculated(position: Position, gameTile: GameTile): Unit =
    removeFollowerGridPane(position)

  override def skipFollowerPlacement(position: Position): Unit =
    removeFollowerGridPane(position)
    notifySkipFollowerPlacement()