package carcassonne.observers.observers.view

import carcassonne.model.game.Player
import carcassonne.model.tile.{GameTile, TileSegment}
import carcassonne.util.Position
import scalafx.scene.layout.Region

/**
 * A trait representing an observer for the game view.
 *
 * This trait defines the contract for observing changes in the game view, such as tile placements,
 * follower placements, and skipping follower placements.
 *
 * @tparam S the type of the subject being observed
 */
trait ObserverGameMatchView:

  /**
   * Called when a tile placement attempt is made.
   *
   * This method is invoked to notify the observer about an attempt to place a tile on the game map.
   *
   * @param gameTile The tile that is being placed.
   * @param position The position where the tile placement was attempted.
   * @return A `Try` indicating the success or failure of the tile placement.
   */
  def placeTile(gameTile: GameTile, position: Position): Unit

  /**
   * Called when a follower placement attempt is made.
   *
   * This method is invoked to notify the observer about an attempt to place a follower on a tile segment.
   *
   * @param position The position on the game map where the follower is being placed.
   * @param segment  The tile segment where the follower is being placed.
   * @param player   The player who is placing the follower.
   * @return A `Try` indicating the success or failure of the follower placement.
   */
  def placeFollower(position: Position, segment: TileSegment, player: Player): Unit

  /**
   * Called when the player decides to skip follower placement.
   *
   * This method is invoked to notify the observer that the player has chosen to skip placing a follower.
   *
   * @return A `Try` indicating the success or failure of the action.
   */
  def skipFollowerPlacement(): Unit