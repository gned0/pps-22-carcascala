package mainApplication

import observers.ObserverGameView
import scalafx.scene.layout.Region

/**
 * The controller for the game map.
 * @param model the game map model
 * @param view the game map view
 */
class GameMapController(model: GameMap, view: GameMapView) extends ObserverGameView[GameMapView]:

  /**
   * Initializes the controller by setting up the view to listen for tile click events.
   */
  def initialize(): Unit =
    view.addObserver(this)

  /**
   * Places a tile at the specified position in the model.
   * @param position the position where the tile should be placed
   */
  def placeTile(position: Position): Unit =
    try
      val tile = GameTile.startTile // Create a new tile
      model.placeTile(tile, position)
      log(s"Placed tile at $position")
    catch
      case e: IllegalArgumentException => println(e.getMessage)

  /**
   * Called when a tile placement attempt is made.
   * @param position the position where the tile placement was attempted
   */
  override def receiveTilePlacementAttempt(position: Position): Unit =
    placeTile(position)

  /**
   * Logs a message to the console.
   * @param string the message to log
   */
  def log(string: String): Unit =
    print(s"CONTROLLER - " + string + "\n")