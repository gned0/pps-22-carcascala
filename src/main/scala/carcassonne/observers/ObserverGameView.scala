package carcassonne.observers

import carcassonne.model.board.Position
import carcassonne.model.tile.GameTile
import scalafx.scene.layout.Region

/**
 * A trait representing an observer for the game view.
 * @tparam S the type of the subject being observed
 */
trait ObserverGameView[S]:

  /**
   * Called when a tile placement attempt is made.
   * @param position the position where the tile placement was attempted
   */
  def receiveTilePlacementAttempt(gameTile: GameTile, position: Position): Unit