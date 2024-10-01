package carcassonne.observers.subjects.view

import carcassonne.model.game.Player
import carcassonne.model.tile.GameTile
import carcassonne.observers.observers.view.ObserverStarterView
import carcassonne.util.Position
import scala.collection.mutable.ListBuffer
import scala.util.Try

/** A trait representing a subject in the observer pattern for the starter view.
  *
  * This trait manages a list of observers and provides methods to add observers and notify them of various game events.
  */
trait SubjectStarterView:
  private val observers: ListBuffer[ObserverStarterView] = ListBuffer.empty

  /** Adds an observer to the subject.
    *
    * @param observer
    *   the observer to add
    */
  def addObserver(observer: ObserverStarterView): Unit =
    observers += observer

  /** Returns the list of observers.
    *
    * @return
    *   the list of observers
    */
  def getObservers: List[ObserverStarterView] = observers.toList

  /** Notifies all observers to switch to the main game view.
    */
  def notifySwitchMainGameView(): Unit =
    observers.foreach(_.switchMainGameView())
