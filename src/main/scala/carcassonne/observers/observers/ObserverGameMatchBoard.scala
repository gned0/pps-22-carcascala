package carcassonne.observers.observers

import carcassonne.model.game.Player
import carcassonne.model.tile.GameTile
import carcassonne.util.Position

trait ObserverGameMatchBoard {

  /**
   * Called when a tile is placed on the game map.
   *
   * @param isTilePlaced whether the tile was successfully placed
   * @param tiles        the current state of the game map tiles
   * @param position     the position where the tile was placed
   */
  def isTilePlaced(isTilePlaced: Boolean,
                   tiles: Option[Map[Position, GameTile]],
                   position: Position): Unit

  def gameEnded(players: List[Player]): Unit
  
}
