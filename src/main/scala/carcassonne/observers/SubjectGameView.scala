package carcassonne.observers

import carcassonne.model.board.Position
import carcassonne.model.tile.GameTile
import scalafx.scene.layout.Region

/**
 * A trait representing a subject in the observer pattern for the game view.
 * @tparam S the type of the subject
 */
trait SubjectGameView[S]:
  this: S =>

  private var observers: List[ObserverGameView[S]] = Nil

  /**
   * Adds an observer to the subject.
   * @param observer the observer to add
   */
  def addObserver(observer: ObserverGameView[S]): Unit = observers = observer :: observers

  /**
   * Returns the list of observers.
   * @return the list of observers
   */
  def getObservers: List[ObserverGameView[S]] = observers

  /**
   * Notifies all observers of a tile placement attempt.
   * @param position the position where the tile placement was attempted
   */
  def notifyTilePlacementAttempt(gameTile: GameTile, position: Position): Unit = observers.foreach(_.receiveTilePlacementAttempt(gameTile, position))