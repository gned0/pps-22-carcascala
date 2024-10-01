package carcassonne.observers.observers.view

import carcassonne.model.game.Player
import carcassonne.model.tile.GameTile
import carcassonne.util.Position
import scalafx.scene.image.{Image, ImageView}

/** A trait representing an observer for the game menu view.
  *
  * This trait defines the contract for observing changes in the game menu view, such as setting the current player, getting and setting the drawn tile, skipping follower
  * placement, and ending the game early.
  */
trait ObserverGameMenuView:

  /** A tuple representing the currently drawn tile and its corresponding image view.
    */
  private var _drawnTile: (GameTile, ImageView) =
    (
      GameTile.createStartTile(),
      new ImageView(
        new Image(s"tiles/${GameTile.createStartTile().imagePath}")
      ):
        fitWidth = 100
        fitHeight = 100
        preserveRatio = true
    )

  /** The current player in the game.
    */
  private var currentPlayer: Option[Player] = None

  /** Sets the current player.
    *
    * @param player
    *   The player to be set as the current player.
    */
  def setCurrentPlayer(player: Player): Unit =
    currentPlayer = Some(player)

  /** Gets the current player.
    *
    * @return
    *   An option containing the current player if set, otherwise None.
    */
  def getCurrentPlayer: Option[Player] = currentPlayer

  /** Gets the currently drawn tile and its image view.
    *
    * @return
    *   A tuple containing the currently drawn tile and its image view.
    */
  def getDrawnTile: (GameTile, ImageView) = _drawnTile

  /** Sets the drawn tile and its image view.
    *
    * @param drawnTile
    *   A tuple containing the tile and its image view to be set as the drawn tile.
    */
  def setDrawnTile(drawnTile: (GameTile, ImageView)): Unit =
    _drawnTile = drawnTile

  /** Skips the follower placement.
    *
    * @param position
    *   An optional position where the follower placement is skipped.
    */
  def skipFollowerPlacement(position: Option[Position]): Unit

  /** Ends the game early.
    */
  def endGameEarly(): Unit
