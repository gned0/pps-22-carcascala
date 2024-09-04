package carcassonne.observers.subjects

import carcassonne.model.tile.{GameTile, GameTileFactory}
import carcassonne.observers.observers.{ObserverGameMatchView, ObserverGameMenuView}
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