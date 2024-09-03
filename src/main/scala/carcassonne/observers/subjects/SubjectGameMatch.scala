package carcassonne.observers.subjects

import carcassonne.model.game.Player
import carcassonne.model.tile.GameTile
import carcassonne.observers.observers.{ObserverGameMatchBoard, ObserverGameMatchMenu}
import carcassonne.util.Position

/**
 * A trait representing a subject in the observer pattern for the game map.
 *
 */
trait SubjectGameMatch:
  private var observersBoardView: List[ObserverGameMatchBoard] = Nil
  private var observersMenuView: List[ObserverGameMatchMenu] = Nil

  def addObserverBoard(observer: ObserverGameMatchBoard): Unit = observersBoardView = observer :: observersBoardView
  def addObserverMenu(observer: ObserverGameMatchMenu): Unit = observersMenuView = observer :: observersMenuView


  /**
   * Returns the list of observers.
   * @return the list of observers
   */
  def getObserversBoard: List[ObserverGameMatchBoard] = observersBoardView
  def getObserversMenu: List[ObserverGameMatchMenu] = observersMenuView


  def notifyTileDrawn(tileDrawn: GameTile): Unit =
    observersMenuView.foreach(_.tileDrawn(tileDrawn))

  def notifyGameEnded(players: List[Player]): Unit =
    observersBoardView.foreach(_.gameEnded(players))

  /**
   * Notifies all observers that a tile has been placed.
   *
   * @param isTilePlaced whether the tile was successfully placed
   * @param tiles        the current state of the game map tiles
   * @param position     the position where the tile was placed
   */
  def notifyIsTilePlaced(isTilePlaced: Boolean,
                         tiles: Option[Map[Position, GameTile]],
                         position: Position): Unit =
    observersBoardView.foreach(_.isTilePlaced(isTilePlaced, tiles, position))