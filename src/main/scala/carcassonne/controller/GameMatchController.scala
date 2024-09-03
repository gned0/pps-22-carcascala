package carcassonne.controller

import carcassonne.model.board.CarcassonneBoard
import carcassonne.model.game.GameMatch
import carcassonne.model.tile.GameTile
import carcassonne.observers.observers.ObserverGameMatchView
import carcassonne.util.{Logger, Position}
import carcassonne.view.GameMatchView
import scalafx.scene.layout.Region

/**
 * The controller for the game map.
 * @param model the game map model
 * @param view the game map view
 */
class GameMatchController(model: GameMatch, view: GameMatchView) extends ObserverGameMatchView :

  /**
   * Initializes the controller by setting up the view to listen for tile click events.
   */
  def initialize(): Unit =
    view.addObserver(this)

  /**
   * Places a tile at the specified position in the model.
   * @param position the position where the tile should be placed
   */
  def placeTile(gameTile: GameTile, position: Position): Unit =
    try
      model.placeTile(gameTile, position)
      Logger.log(s"CONTROLLER", s"Placed tile at $position")
    catch
      case e: IllegalArgumentException => println(e.getMessage)

  /**
   * Called when a tile placement attempt is made.
   * @param position the position where the tile placement was attempted
   */
  override def receiveTilePlacementAttempt(gameTile: GameTile, position: Position): Unit =
    placeTile(gameTile, position)