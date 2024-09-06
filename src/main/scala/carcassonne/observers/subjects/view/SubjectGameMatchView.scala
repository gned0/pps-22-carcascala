package carcassonne.observers.subjects.view

import carcassonne.model.game.Player
import carcassonne.model.tile.{GameTile, TileSegment}
import carcassonne.observers.observers.ObserverGameMatchView
import carcassonne.util.Position
import scalafx.scene.layout.Region

/**
 * A trait representing a subject in the observer pattern for the game view.
 * @tparam S the type of the subject
 */
trait SubjectGameMatchView:
  private var observers: List[ObserverGameMatchView] = Nil

  /**
   * Adds an observer to the subject.
   * @param observer the observer to add
   */
  def addObserver(observer: ObserverGameMatchView): Unit = observers = observer :: observers

  /**
   * Returns the list of observers.
   * @return the list of observers
   */
  def getObservers: List[ObserverGameMatchView] = observers

  /**
   * Notifies all observers of a tile placement attempt.
   * @param position the position where the tile placement was attempted
   */
  def notifyTilePlacementAttempt(gameTile: GameTile, position: Position): Unit =
    observers.foreach(_.placeTile(gameTile, position))

  def notifyFollowerPlacement(gameTile: GameTile, segment: TileSegment, player: Player): Unit =
    observers.foreach(_.placeFollower(gameTile, segment, player))