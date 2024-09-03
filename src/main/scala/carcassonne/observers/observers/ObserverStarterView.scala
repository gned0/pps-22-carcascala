package carcassonne.observers.observers

import carcassonne.model.game.Player
import carcassonne.model.tile.GameTile
import carcassonne.util.Position

/**
 * A trait representing an observer for the game map.
 *
 * @tparam S the type of the subject being observed
 */
trait ObserverStarterView:

  def switchMainGameView(): Unit