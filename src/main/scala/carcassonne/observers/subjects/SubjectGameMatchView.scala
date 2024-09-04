package carcassonne.observers.subjects

import carcassonne.model.tile.GameTile
import carcassonne.observers.observers.ObserverGameMatchView
import carcassonne.util.Position
import scalafx.scene.layout.Region

/**
 * A trait representing a subject in the observer pattern for the game view.
 * @tparam S the type of the subject
 */
trait SubjectGameMatchView[S]:
  this: S =>

  private var observers: List[ObserverGameMatchView[S]] = Nil

  /**
   * Adds an observer to the subject.
   * @param observer the observer to add
   */
  def addObserver(observer: ObserverGameMatchView[S]): Unit = observers = observer :: observers

  /**
   * Returns the list of observers.
   * @return the list of observers
   */
  def getObservers: List[ObserverGameMatchView[S]] = observers

  /**
   * Notifies all observers of a tile placement attempt.
   * @param position the position where the tile placement was attempted
   */
  def notifyTilePlacementAttempt(gameTile: GameTile, position: Position): Unit = observers.foreach(_.placeTile(gameTile, position))