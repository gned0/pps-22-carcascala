package carcassonne.observers

import carcassonne.model.{GameTile, Position}
/**
 * A trait representing an observer for the game map.
 * @tparam S the type of the subject being observed
 */
trait ObserverGameMap[S]:

  /**
   * Called when a tile is placed on the game map.
   * @param isTilePlaced whether the tile was successfully placed
   * @param tiles the current state of the game map tiles
   * @param position the position where the tile was placed
   */
  def isTilePlaced(isTilePlaced: Boolean,
                   tiles: Option[Map[Position, GameTile]],
                   position: Position): Unit