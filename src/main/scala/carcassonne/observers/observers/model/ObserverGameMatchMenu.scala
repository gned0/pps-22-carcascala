package carcassonne.observers.observers.model

import carcassonne.model.game.Player
import carcassonne.model.tile.{GameTile, TileSegment}
import carcassonne.util.Position

/**
 * Trait representing an observer for the game match menu.
 *
 * This trait defines the contract for observing changes in the game match menu, such as tile draws,
 * player changes, available follower positions, and scoreboard updates.
 */
trait ObserverGameMatchMenu:

  /**
   * The current player in the game.
   */
  private var currentPlayer: Option[Player] = None

  /**
   * Sets the current player.
   *
   * @param player The player to set as the current player.
   */
  def setCurrentPlayer(player: Player): Unit =
    currentPlayer = Some(player)

  /**
   * Gets the current player.
   *
   * @return An option containing the current player, or None if no player is set.
   */
  def getCurrentPlayer: Option[Player] = currentPlayer

  /**
   * Called when a tile is drawn.
   *
   * This method is invoked to notify the observer about the drawing of a tile.
   *
   * @param tileDrawn The tile that was drawn.
   * @param tilesCount The number of tiles remaining.
   */
  def tileDrawn(tileDrawn: GameTile, tilesCount: Int): Unit

  /**
   * Called when the current player changes.
   *
   * This method is invoked to notify the observer about a change in the current player.
   *
   * @param player The player who is now the current player.
   */
  def playerChanged(player: Player): Unit

  /**
   * Called to provide available follower positions.
   *
   * This method is invoked to notify the observer about the available positions for placing followers.
   *
   * @param availSegments A list of tile segments where followers can be placed.
   * @param position The position on the game map related to the available segments.
   */
  def availableFollowerPositions(availSegments: List[TileSegment], position: Position): Unit

  /**
   * Called to update the scoreboard.
   *
   * This method is invoked to notify the observer about the updated scores of the players.
   *
   * @param scores A map of players to their respective scores.
   */
  def updateScoreboard(scores: Map[Player, Int]): Unit