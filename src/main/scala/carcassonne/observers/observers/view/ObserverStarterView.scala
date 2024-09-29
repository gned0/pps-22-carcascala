package carcassonne.observers.observers.view

import carcassonne.model.game.Player
import carcassonne.model.tile.GameTile
import carcassonne.util.Position

/**
 * A trait representing an observer for the game map.
 *
 * This trait defines the contract for observing changes in the game map, such as switching the main game view.
 */
trait ObserverStarterView:

  /**
   * Switches the main game view.
   *
   * This method is invoked to switch the current view to the main game view.
   */
  def switchMainGameView(): Unit