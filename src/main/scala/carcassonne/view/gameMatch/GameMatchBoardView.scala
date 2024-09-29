package carcassonne.view.gameMatch

import carcassonne.model.game.Player
import carcassonne.model.tile.{GameTile, TileSegment}
import carcassonne.observers.observers.model.ObserverGameMatchBoard
import carcassonne.observers.observers.view.ObserverGameMenuView
import carcassonne.observers.subjects.view.SubjectGameMatchView
import carcassonne.util.Position
import carcassonne.view.gameEnd.GameEndView
import javafx.scene.layout.GridPane.{getColumnIndex, getRowIndex}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.{Cursor, Node}
import scalafx.scene.input.{MouseButton, MouseEvent}
import scalafx.scene.layout.{Background, BackgroundFill, Border, BorderStroke, BorderStrokeStyle, BorderWidths, CornerRadii, GridPane, Priority, Region}
import scalafx.Includes.*
import scalafx.scene.layout.Priority.Always
import scalafx.scene.paint.Color

/**
 * Represents the view for the game match board in the Carcassonne game.
 *
 * @param gameEndedSwitchView A function to switch the view when the game ends.
 */
class GameMatchBoardView(gameEndedSwitchView: () => Unit) extends GridPane
  with ObserverGameMenuView
  with SubjectGameMatchView
  with ObserverGameMatchBoard:

  this.hgrow = Always
  this.vgrow = Always
  this.alignment = Pos.Center

  /**
   * Places a tile at the specified position on the board.
   *
   * @param position The position where the tile should be placed.
   * @param tileGraphicElement The graphical representation of the tile.
   */
  private def placeTile(position: Position, tileGraphicElement: Node): Unit =
    this.getChildren.removeIf(node =>
      getColumnIndex(node) == position.x && getRowIndex(node) == position.y
    )
    this.add(tileGraphicElement, position.x, position.y)

  /**
   * Creates new placeholder tiles around the specified position.
   *
   * @param position The position around which new placeholders should be created.
   */
  private def createNewPlaceholders(position: Position): Unit =
    Seq(position.x - 1, position.x + 1).foreach { posX =>
      if !this.getChildren.exists(node =>
        getColumnIndex(node) == posX && getRowIndex(node) == position.y
      ) then
        createPlaceholderTile(Position(posX, position.y))
    }
    Seq(position.y - 1, position.y + 1).foreach { posY =>
      if !this.getChildren.exists(node =>
        getColumnIndex(node) == position.x && getRowIndex(node) == posY
      ) then
        createPlaceholderTile(Position(position.x, posY))
    }

  /**
   * Creates a placeholder tile at the specified position.
   *
   * @param position The position where the placeholder tile should be created.
   * @return The created placeholder tile.
   */
  private def createPlaceholderTile(position: Position): Region =
    new Region:
      prefWidth = 100
      prefHeight = 100
      border = new Border(new BorderStroke(
        Color.Black,
        BorderStrokeStyle.Solid,
        CornerRadii.Empty,
        new BorderWidths(1)
      ))
      onMouseEntered = _ =>
        this.background = new Background(Array(
          new BackgroundFill(
            Color.DarkGray,
            CornerRadii.Empty,
            Insets.Empty
          ))
        )
        this.cursor = Cursor.Hand
      onMouseExited = _ =>
        this.background = Background.Empty
        this.cursor = Cursor.Default
      onMouseClicked = (event: MouseEvent) => if event.button == MouseButton.Primary then
        notifyTilePlacementAttempt(getDrawnTile._1, position)
      add(this, position.x, position.y)

  /**
   * Removes the follower grid pane at the specified position.
   *
   * @param position The position from which the follower grid pane should be removed.
   */
  private def removeFollowerGridPane(position: Position): Unit =
    this.getChildrenUnmodifiable.toArray
      .collectFirst {
        case child: javafx.scene.Node if getColumnIndex(child) == position.x &&
          getRowIndex(child) == position.y => child
      }
      .collect {
        case stackPane: javafx.scene.layout.StackPane => stackPane.getChildren.remove(1)
      }
      .getOrElse(println("No element or other type of element"))

  /**
   * Updates the board when a tile is placed.
   *
   * @param isTilePlaced Indicates whether the tile was successfully placed.
   * @param position The position where the tile was placed.
   */
  override def isTilePlaced(isTilePlaced: Boolean,
                            position: Position): Unit =
    if isTilePlaced then
      placeTile(position, getDrawnTile._2)
      if position.equals(Position(500, 500)) then createNewPlaceholders(position)

  /**
   * Handles the end of the game.
   *
   * @param players The list of players in the game.
   */
  override def gameEnded(players: List[Player]): Unit =
    GameEndView(players).popupStage.show()
    gameEndedSwitchView()

  /**
   * Updates the current player.
   *
   * @param player The new current player.
   */
  override def playerChanged(player: Player): Unit =
    setCurrentPlayer(player)

  /**
   * Displays the available follower positions on the board.
   *
   * @param availSegments The list of available tile segments for placing followers.
   * @param position The position of the tile on the board.
   */
  override def availableFollowerPositions(availSegments: List[TileSegment], position: Position): Unit =
    placeTile(position,
      GameMatchFollowerGridView(
        availSegments,
        getDrawnTile._2,
        getCurrentPlayer,
        position,
        notifyFollowerPlacement,
        createNewPlaceholders
      )
    )

  /**
   * Updates the board when the score is calculated.
   *
   * @param position The position of the tile on the board.
   * @param gameTile The game tile for which the score was calculated.
   */
  override def scoreCalculated(position: Position, gameTile: GameTile): Unit =
    removeFollowerGridPane(position)

  /**
   * Skips the follower placement.
   *
   * @param position The position of the tile on the board.
   */
  override def skipFollowerPlacement(position: Option[Position]): Unit =
    position.foreach(removeFollowerGridPane)
    createNewPlaceholders(position.get)
    notifySkipFollowerPlacement()

  /**
   * Ends the game early and switches the view.
   */
  override def endGameEarly(): Unit =
    gameEndedSwitchView()