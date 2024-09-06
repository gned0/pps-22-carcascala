package carcassonne.observers.subjects.view

import carcassonne.model.game.Player
import carcassonne.model.tile.GameTile
import carcassonne.observers.observers.ObserverStarterView
import carcassonne.util.Position

/**
 * A trait representing a subject in the observer pattern for the game map.
 *
 * @tparam S the type of the subject
 */
trait SubjectStarterView:
  private var observers: List[ObserverStarterView] = Nil

  /**
   * Adds an observer to the subject.
   * @param observer the observer to add
   */
  def addObserver(observer: ObserverStarterView): Unit = observers = observer :: observers

  /**
   * Returns the list of observers.
   * @return the list of observers
   */
  def getObservers: List[ObserverStarterView] = observers

  def notifySwitchMainGameView(): Unit =
    observers.foreach(_.switchMainGameView())