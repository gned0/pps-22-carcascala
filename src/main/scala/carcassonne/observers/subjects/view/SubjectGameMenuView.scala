package carcassonne.observers.subjects.view

import carcassonne.model.tile.GameTile
import carcassonne.observers.observers.view.{ObserverGameMatchView, ObserverGameMenuView}
import carcassonne.util.Position
import scalafx.scene.image.{Image, ImageView}

trait SubjectGameMenuView:
  private var observers: List[ObserverGameMenuView] = Nil

  /**
   * Adds an observer to the subject.
   *
   * @param observer the observer to add
   */
  def addObserver(observer: ObserverGameMenuView): Unit = observers = observer :: observers

  /**
   * Returns the list of observers.
   *
   * @return the list of observers
   */
  def getObservers: List[ObserverGameMenuView] = observers

  def setDrawnTile(tile: GameTile, tileImage: ImageView): Unit =
    observers.foreach(_.setDrawnTile((tile, tileImage)))
    
  def notifySkipTurn(position: Option[Position]): Unit =
    observers.foreach(_.skipFollowerPlacement(position))
    
  def notifyEndGameEarly(): Unit =
    observers.foreach(_.endGameEarly())