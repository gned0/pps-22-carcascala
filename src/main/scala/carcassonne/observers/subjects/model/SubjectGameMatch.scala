package carcassonne.observers.subjects.model

import carcassonne.model.game.Player
import carcassonne.model.tile.{GameTile, TileSegment}
import carcassonne.observers.observers.model.{ObserverGameMatchBoard, ObserverGameMatchMenu}
import carcassonne.util.Position

/** A trait representing a subject in the observer pattern for the game map.
 *
 * This trait manages a list of observers for both the board view and the menu view.
 * It provides methods to add observers and notify them of various game events.
 *
 */
trait SubjectGameMatch:

  /** List of observers for the board view. */
  private var observersBoardView: List[ObserverGameMatchBoard] = List.empty

  /** List of observers for the menu view. */
  private var observersMenuView: List[ObserverGameMatchMenu] = List.empty

  /** Adds an observer to the board view observers list.
   *
   * @param observer
   *   the observer to be added
   */
  def addObserverBoard(observer: ObserverGameMatchBoard): Unit =
    observersBoardView = observer :: observersBoardView

  /** Adds an observer to the menu view observers list.
   *
   * @param observer
   *   the observer to be added
   */
  def addObserverMenu(observer: ObserverGameMatchMenu): Unit =
    observersMenuView = observer :: observersMenuView

  /** Retrieves the list of board view observers.
   *
   * @return
   *   the list of board view observers
   */
  def getObserversBoard: List[ObserverGameMatchBoard] = observersBoardView

  /** Retrieves the list of menu view observers.
   *
   * @return
   *   the list of menu view observers
   */
  def getObserversMenu: List[ObserverGameMatchMenu] = observersMenuView

  /** Notifies all board view observers that a tile has been placed.
   *
   * @param isTilePlaced
   *   whether the tile was successfully placed
   * @param position
   *   the position where the tile was placed
   */
  def notifyIsTilePlaced(isTilePlaced: Boolean, position: Position): Unit =
    observersBoardView.foreach(_.isTilePlaced(isTilePlaced, position))

  /** Notifies all menu view observers that a tile has been drawn.
   *
   * @param tileDrawn
   *   the tile that was drawn
   * @param tilesCount
   *   the number of tiles remaining
   */
  def notifyTileDrawn(tileDrawn: GameTile, tilesCount: Int): Unit =
    observersMenuView.foreach(_.tileDrawn(tileDrawn, tilesCount))

  /** Notifies all board view observers that the game has ended.
   *
   * @param players
   *   the list of players in the game
   */
  def notifyGameEnded(players: List[Player]): Unit =
    observersBoardView.foreach(_.gameEnded(players))

  /** Notifies all observers that the current player has changed.
   *
   * @param player
   *   the new current player
   */
  def notifyPlayerChanged(player: Player): Unit =
    observersBoardView.foreach(_.playerChanged(player))
    observersMenuView.foreach(_.playerChanged(player))

  /** Notifies all observers of the available follower positions.
   *
   * @param availSegments
   *   the list of available tile segments for follower placement
   * @param position
   *   the position on the game map
   */
  def notifyAvailableFollowerPositions(availSegments: List[TileSegment], position: Position): Unit =
    observersBoardView.foreach(_.availableFollowerPositions(availSegments, position))
    observersMenuView.foreach(_.availableFollowerPositions(availSegments, position))

  /** Notifies all board view observers that the score has been calculated.
   *
   * @param position
   *   the position on the game map
   * @param gameTile
   *   the game tile at the position
   */
  def notifyScoreCalculated(position: Position, gameTile: GameTile): Unit =
    observersBoardView.foreach(_.scoreCalculated(position, gameTile))

  /** Notifies all menu view observers that the scoreboard has been updated.
   *
   * @param scores
   *   the updated scores for each player
   */
  def notifyScoreboardUpdated(scores: Map[Player, Int]): Unit =
    observersMenuView.foreach(_.updateScoreboard(scores))