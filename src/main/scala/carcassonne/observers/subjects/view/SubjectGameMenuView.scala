package carcassonne.observers.subjects.view

import carcassonne.model.tile.GameTile
import carcassonne.observers.observers.view.ObserverGameMenuView
import carcassonne.util.Position
import scalafx.scene.image.{Image, ImageView}
import scala.collection.mutable.ListBuffer
import scala.util.Try

/** A trait representing a subject in the observer pattern for the game menu view.
  *
  * This trait manages a list of observers and provides methods to add observers and notify them of various game events.
  */
trait SubjectGameMenuView:
  private val observers: ListBuffer[ObserverGameMenuView] = ListBuffer.empty

  /** Adds an observer to the subject.
    *
    * @param observer
    *   the observer to add
    */
  def addObserver(observer: ObserverGameMenuView): Unit =
    observers += observer

  /** Returns the list of observers.
    *
    * @return
    *   the list of observers
    */
  def getObservers: List[ObserverGameMenuView] = observers.toList

  /** Notifies all observers to set the drawn tile.
    *
    * @param tile
    *   the game tile that has been drawn
    * @param tileImage
    *   the image view of the drawn tile
    */
  def setDrawnTile(tile: GameTile, tileImage: ImageView): Unit =
    observers.foreach(_.setDrawnTile(tile, tileImage))

  /** Notifies all observers to skip the turn.
    *
    * @param position
    *   an optional position on the game map
    */
  def notifySkipTurn(position: Option[Position]): Unit =
    observers.foreach(_.skipFollowerPlacement(position))

  /** Notifies all observers to end the game early.
    */
  def notifyEndGameEarly(): Unit =
    observers.foreach(_.endGameEarly())

  /** Attempts to notify all observers to set the drawn tile, returning a Try.
    *
    * @param tile
    *   the game tile that has been drawn
    * @param tileImage
    *   the image view of the drawn tile
    * @return
    *   a Try indicating success or failure
    */
  def trySetDrawnTile(tile: GameTile, tileImage: ImageView): Try[Unit] =
    Try(setDrawnTile(tile, tileImage))

  /** Attempts to notify all observers to skip the turn, returning a Try.
    *
    * @param position
    *   an optional position on the game map
    * @return
    *   a Try indicating success or failure
    */
  def tryNotifySkipTurn(position: Option[Position]): Try[Unit] =
    Try(notifySkipTurn(position))

  /** Attempts to notify all observers to end the game early, returning a Try.
    *
    * @return
    *   a Try indicating success or failure
    */
  def tryNotifyEndGameEarly(): Try[Unit] =
    Try(notifyEndGameEarly())
