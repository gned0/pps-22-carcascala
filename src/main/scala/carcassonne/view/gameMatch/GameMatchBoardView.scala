package carcassonne.view.gameMatch

import carcassonne.model.game.Player
import carcassonne.model.tile.{GameTile, TileSegment}
import carcassonne.observers.observers.model.ObserverGameMatchBoard
import carcassonne.observers.observers.view.ObserverGameMenuView
import carcassonne.observers.subjects.view.SubjectGameMatchView
import carcassonne.util.Position
import carcassonne.view.gameEnd.GameEndView
import javafx.scene.layout.GridPane.{getColumnIndex, getRowIndex}
import scalafx.geometry.Pos
import scalafx.scene.Node
import scalafx.scene.input.{MouseButton, MouseEvent}
import scalafx.scene.layout.{GridPane, Priority, Region}
import scalafx.Includes.*

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

  private def placeTile(position: Position, tileGraphicElement: Node): Unit =
    this.getChildren.removeIf(node =>
      getColumnIndex(node) == position.x && getRowIndex(node) == position.y
    )
    this.add(tileGraphicElement, position.x, position.y)

  private def createNewPlaceholders(tiles: Map[Position, GameTile], position: Position): Unit =
    Seq(position.x - 1, position.x + 1).foreach { posX =>
      if !tiles.contains(Position(posX, position.y)) then
        createPlaceholderTile(Position(posX, position.y))
    }
    Seq(position.y - 1, position.y + 1).foreach { posY =>
      if !tiles.contains(Position(position.x, posY)) then
        createPlaceholderTile(Position(position.x, posY))
    }

  private def createPlaceholderTile(position: Position): Region =
    new Region:
      prefWidth = 100
      prefHeight = 100
      styleClass += "placeholderTile"
      onMouseClicked = (event: MouseEvent) => if event.button == MouseButton.Primary then
        notifyTilePlacementAttempt(getDrawnTile._1, position)
      add(this, position.x, position.y)

  private def removeFollowerGridPane(position: Position): Unit =
    this.getChildrenUnmodifiable.toArray
      .collectFirst {
        case child: javafx.scene.Node if getColumnIndex(child) == position.x && getRowIndex(child) == position.y => child
      }
      .collect {
        case stackPane: javafx.scene.layout.StackPane => stackPane.getChildren.remove(1)
      }
      .getOrElse(println("No element or other type of element"))

  override def isTilePlaced(isTilePlaced: Boolean, tilesOption: Option[Map[Position, GameTile]], position: Position): Unit =
    tilesOption.foreach { tiles =>
      if isTilePlaced then
        placeTile(position, getDrawnTile._2)
        createNewPlaceholders(tiles, position)
    }

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