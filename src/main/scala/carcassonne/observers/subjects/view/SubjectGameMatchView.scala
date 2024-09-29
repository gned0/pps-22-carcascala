package carcassonne.observers.subjects.view

import carcassonne.model.game.Player
import carcassonne.model.tile.{GameTile, TileSegment}
import carcassonne.observers.observers.view.ObserverGameMatchView
import carcassonne.util.Position
import scalafx.scene.layout.Region
import scala.collection.mutable.ListBuffer
import scala.util.Try

/**
 * A trait representing a subject in the observer pattern for the game view.
 *
 * This trait manages a list of observers and provides methods to add observers
 * and notify them of various game events.
 */
trait SubjectGameMatchView:
  private val observers: ListBuffer[ObserverGameMatchView] = ListBuffer.empty

  /**
   * Adds an observer to the subject.
   *
   * @param observer the observer to add
   */
  def addObserver(observer: ObserverGameMatchView): Unit =
    observers += observer

  /**
   * Returns the list of observers.
   *
   * @return the list of observers
   */
  def getObservers: List[ObserverGameMatchView] = observers.toList

  /**
   * Notifies all observers of a tile placement attempt.
   *
   * @param gameTile the tile that is being placed
   * @param position the position where the tile placement was attempted
   */
  def notifyTilePlacementAttempt(gameTile: GameTile, position: Position): Unit =
    observers.foreach(_.placeTile(gameTile, position))

  /**
   * Notifies all observers of a follower placement.
   *
   * @return a function that takes a position, a tile segment, and a player, and notifies all observers
   */
  def notifyFollowerPlacement: (Position, TileSegment, Player) => Unit =
    (position, segment, player) =>
      observers.foreach(_.placeFollower(position, segment, player))

  /**
   * Notifies all observers to skip follower placement.
   */
  def notifySkipFollowerPlacement(): Unit =
    observers.foreach(_.skipFollowerPlacement())

  /**
   * Attempts to notify all observers of a tile placement attempt, returning a Try.
   *
   * @param gameTile the tile that is being placed
   * @param position the position where the tile placement was attempted
   * @return a Try indicating success or failure
   */
  def tryNotifyTilePlacementAttempt(gameTile: GameTile, position: Position): Try[Unit] =
    Try(notifyTilePlacementAttempt(gameTile, position))

  /**
   * Attempts to notify all observers of a follower placement, returning a Try.
   *
   * @param position the position where the follower is being placed
   * @param segment the tile segment where the follower is being placed
   * @param player the player placing the follower
   * @return a Try indicating success or failure
   */
  def tryNotifyFollowerPlacement(position: Position, segment: TileSegment, player: Player): Try[Unit] =
    Try(notifyFollowerPlacement(position, segment, player))

  /**
   * Attempts to notify all observers to skip follower placement, returning a Try.
   *
   * @return a Try indicating success or failure
   */
  def tryNotifySkipFollowerPlacement(): Try[Unit] =
    Try(notifySkipFollowerPlacement())