package carcassonne.observers.subjects.model

import carcassonne.model.game.Player
import carcassonne.model.tile.{GameTile, TileSegment}
import carcassonne.observers.observers.model.{ObserverGameMatchBoard, ObserverGameMatchMenu}
import carcassonne.util.Position

/** A trait representing a subject in the observer pattern for the game map.
  *
  * @tparam S
  *   the type of the subject
  */
trait SubjectGameMatch:
  private var observersBoardView: List[ObserverGameMatchBoard] = Nil
  private var observersMenuView: List[ObserverGameMatchMenu] = Nil

  def addObserverBoard(observer: ObserverGameMatchBoard): Unit = observersBoardView = observer :: observersBoardView
  def addObserverMenu(observer: ObserverGameMatchMenu): Unit = observersMenuView = observer :: observersMenuView

  def getObserversBoard: List[ObserverGameMatchBoard] = observersBoardView
  def getObserversMenu: List[ObserverGameMatchMenu] = observersMenuView

  /** Notifies all observers that a tile has been placed.
    * @param isTilePlaced
    *   whether the tile was successfully placed
    * @param tiles
    *   the current state of the game map tiles
    * @param position
    *   the position where the tile was placed
    */
  def notifyIsTilePlaced(isTilePlaced: Boolean, tiles: Option[Map[Position, GameTile]], position: Position): Unit =
    observersBoardView.foreach(_.isTilePlaced(isTilePlaced, tiles, position))

  def notifyTileDrawn(tileDrawn: GameTile, tilesCount: Int): Unit =
    observersMenuView.foreach(_.tileDrawn(tileDrawn, tilesCount))

  def notifyGameEnded(players: List[Player]): Unit =
    observersBoardView.foreach(_.gameEnded(players))

  def notifyPlayerChanged(player: Player): Unit =
    observersBoardView.foreach(_.playerChanged(player))
    observersMenuView.foreach(_.playerChanged(player))

  def notifyAvailableFollowerPositions(availSegments: List[TileSegment], position: Position): Unit =
    observersBoardView.foreach(_.availableFollowerPositions(availSegments, position))
    observersMenuView.foreach(_.availableFollowerPositions(availSegments, position))

  def notifyScoreCalculated(position: Position, gameTile: GameTile): Unit =
    observersBoardView.foreach(_.scoreCalculated(position, gameTile))

  def notifyScoreboardUpdated(scores: Map[Player, Int]): Unit =
    observersMenuView.foreach(_.updateScoreboard(scores))
