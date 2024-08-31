package carcassonne.observers

import carcassonne.model.board.Position
import carcassonne.model.game.Player
import carcassonne.model.tile.GameTile

/**
 * A trait representing a subject in the observer pattern for the game map.
 *
 * @tparam S the type of the subject
 */
trait SubjectGameMatch[S]:
  this: S =>

  private var observers: List[ObserverGameMatch[S]] = Nil

  /**
   * Adds an observer to the subject.
   * @param observer the observer to add
   */
  def addObserver(observer: ObserverGameMatch[S]): Unit = observers = observer :: observers

  /**
   * Returns the list of observers.
   * @return the list of observers
   */
  def getObservers: List[ObserverGameMatch[S]] = observers

  /**
   * Notifies all observers that a tile has been placed.
   * @param isTilePlaced whether the tile was successfully placed
   * @param tiles the current state of the game map tiles
   * @param position the position where the tile was placed
   */
  def notifyIsTilePlaced(isTilePlaced: Boolean,
                         tiles: Option[Map[Position, GameTile]],
                         position: Position): Unit =
    observers.foreach(_.isTilePlaced(isTilePlaced, tiles, position))
    
  def notifyTileDrawn(tileDrawn: GameTile): Unit =
    observers.foreach(_.tileDrawn(tileDrawn))
    
  def notifyGameEnded(players: List[Player]): Unit =
    observers.foreach(_.gameEnded(players))  