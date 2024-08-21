package carcassonne.observers

import carcassonne.model.{GameTile, Position}
/**
 * A trait representing a subject in the observer pattern for the game map.
 * @tparam S the type of the subject
 */
trait SubjectGameMap[S]:
  this: S =>

  private var observers: List[ObserverGameMap[S]] = Nil

  /**
   * Adds an observer to the subject.
   * @param observer the observer to add
   */
  def addObserver(observer: ObserverGameMap[S]): Unit = observers = observer :: observers

  /**
   * Returns the list of observers.
   * @return the list of observers
   */
  def getObservers: List[ObserverGameMap[S]] = observers

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