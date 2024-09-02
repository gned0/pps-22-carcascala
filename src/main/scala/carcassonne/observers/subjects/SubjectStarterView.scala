package carcassonne.observers.subjects

import carcassonne.model.game.Player
import carcassonne.model.tile.GameTile
import carcassonne.observers.observers.ObserverStarterView
import carcassonne.util.Position

/**
 * A trait representing a subject in the observer pattern for the game map.
 *
 * @tparam S the type of the subject
 */
trait SubjectStarterView[S]:
  this: S =>

  private var observers: List[ObserverStarterView[S]] = Nil

  /**
   * Adds an observer to the subject.
   * @param observer the observer to add
   */
  def addObserver(observer: ObserverStarterView[S]): Unit = observers = observer :: observers

  /**
   * Returns the list of observers.
   * @return the list of observers
   */
  def getObservers: List[ObserverStarterView[S]] = observers

  def notifySwitchMainGameView(): Unit =
    observers.foreach(_.switchMainGameView())