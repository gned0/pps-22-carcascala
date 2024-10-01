package carcassonne.observers.observers.model

import carcassonne.model.game.Player
import carcassonne.model.tile.{GameTile, TileSegment}
import carcassonne.util.Position

/** A trait representing an observer for the game map.
  *
  * This trait defines the contract for observing changes in the game map, such as tile placements, game end events, player changes, available follower positions, and
  * score calculations.
  */
trait ObserverGameMatchBoard:

  /** Called when a tile is placed on the game map.
    *
    * This method is invoked to notify the observer about the placement of a tile on the game map.
    *
    * @param isTilePlaced
    *   A boolean indicating whether the tile was successfully placed.
    * @param position
    *   The position where the tile was placed.
    */
  def isTilePlaced(isTilePlaced: Boolean, position: Position): Unit

  /** Called when the game ends.
    *
    * This method is invoked to notify the observer that the game has ended and provides the final list of players.
    *
    * @param players
    *   A list of players who participated in the game.
    */
  def gameEnded(players: List[Player]): Unit

  /** Called when the current player changes.
    *
    * This method is invoked to notify the observer about a change in the current player.
    *
    * @param player
    *   The player who is now the current player.
    */
  def playerChanged(player: Player): Unit

  /** Called to provide available follower positions.
    *
    * This method is invoked to notify the observer about the available positions for placing followers.
    *
    * @param availSegments
    *   A list of tile segments where followers can be placed.
    * @param position
    *   The position on the game map related to the available segments.
    */
  def availableFollowerPositions(availSegments: List[TileSegment], position: Position): Unit

  /** Called when the score is calculated for a tile.
    *
    * This method is invoked to notify the observer about the score calculation for a specific tile.
    *
    * @param position
    *   The position of the tile for which the score was calculated.
    * @param gameTile
    *   The game tile for which the score was calculated.
    */
  def scoreCalculated(position: Position, gameTile: GameTile): Unit
