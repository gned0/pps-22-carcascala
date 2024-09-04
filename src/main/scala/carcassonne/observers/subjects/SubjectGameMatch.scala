package carcassonne.observers.subjects

import carcassonne.model.game.Player
import carcassonne.model.tile.{GameTile, TileSegment}
import carcassonne.observers.observers.{ObserverGameMatchBoard, ObserverGameMatchMenu}
import carcassonne.util.Position

/**
 * A trait representing a subject in the observer pattern for the game map.
 *
 * @tparam S the type of the subject
 */
trait SubjectGameMatch:
  private var observersBoardView: List[ObserverGameMatchBoard] = Nil
  private var observersMenuView: List[ObserverGameMatchMenu] = Nil
  
  def addObserverBoard(observer: ObserverGameMatchBoard): Unit = observersBoardView = observer :: observersBoardView
  def addObserverMenu(observer: ObserverGameMatchMenu): Unit = observersMenuView = observer :: observersMenuView
  
  def getObserversBoard: List[ObserverGameMatchBoard] = observersBoardView
  def getObserversMenu: List[ObserverGameMatchMenu] = observersMenuView

  /**
   * Notifies all observers that a tile has been placed.
   * @param isTilePlaced whether the tile was successfully placed
   * @param tiles the current state of the game map tiles
   * @param position the position where the tile was placed
   */
  def notifyIsTilePlaced(isTilePlaced: Boolean,
                         tiles: Option[Map[Position, GameTile]],
                         position: Position): Unit =
    observersBoardView.foreach(_.isTilePlaced(isTilePlaced, tiles, position))
    
  def notifyTileDrawn(tileDrawn: GameTile): Unit =
    observersMenuView.foreach(_.tileDrawn(tileDrawn))
    
  def notifyGameEnded(players: List[Player]): Unit =
    observersBoardView.foreach(_.gameEnded(players))  
    
  def notifyIsFollowerPlaced(gameTile: GameTile, segment: TileSegment, player: Player): Unit =
    observersBoardView.foreach(_.isFollowerPlaced(gameTile, segment, player))

  def notifyPlayerChanged(player: Player): Unit =
    observersBoardView.foreach(_.playerChanged(player))