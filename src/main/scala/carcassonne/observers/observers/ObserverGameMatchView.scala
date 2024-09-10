package carcassonne.observers.observers

import carcassonne.model.game.Player
import carcassonne.model.tile.{GameTile, TileSegment}
import carcassonne.util.Position
import scalafx.scene.layout.Region

/**
 * A trait representing an observer for the game view.
 * @tparam S the type of the subject being observed
 */
trait ObserverGameMatchView:

  /**
   * Called when a tile placement attempt is made.
   * @param position the position where the tile placement was attempted
   */
  def placeTile(gameTile: GameTile, position: Position): Unit
  
  def placeFollower(gameTile: GameTile, segment: TileSegment, player: Player): Unit
  
  def skipFollowerPlacement(): Unit