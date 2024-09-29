package carcassonne.controller

import carcassonne.model.game.{GameState, Player}
import carcassonne.model.tile.{GameTile, TileSegment}
import carcassonne.observers.observers.view.ObserverGameMatchView
import carcassonne.util.Position
import carcassonne.view.gameMatch.GameMatchBoardView

/**
 * Trait representing the game controller.
 *
 * This trait defines the primary operations for controlling the game flow,
 * including initializing the game, placing tiles and followers, and progressing to the next turn.
 */
trait GameController:

  /**
   * Initializes the game by setting up the initial state and drawing the first tile.
   */
  def initialize(): Unit

  /**
   * Places a tile on the game board at the specified position.
   *
   * @param gameTile the tile to place
   * @param position the position on the board to place the tile
   */
  def placeTile(gameTile: GameTile, position: Position): Unit

  /**
   * Places a follower on a specific segment of a tile at the given position.
   *
   * @param position the position on the board where the follower is placed
   * @param segment the segment of the tile where the follower is placed
   * @param player the player placing the follower
   */
  def placeFollower(position: Position, segment: TileSegment, player: Player): Unit

  /**
   * Proceeds to the next turn in the game.
   */
  def nextTurn(): Unit

/**
 * Companion object for the GameController trait.
 *
 * Provides a factory method for creating an instance of GameController.
 */
object GameController:

  /**
   * Creates an instance of GameController.
   *
   * @param model the game state model
   * @param view the game match board view
   * @return an instance of GameController
   */
  def apply(model: GameState, view: GameMatchBoardView): GameController = new GameControllerImpl(model, view)

  /**
   * Private implementation of the GameController trait.
   *
   * @param model the game state model
   * @param view the game match board view
   */
  private class GameControllerImpl(model: GameState, view: GameMatchBoardView) extends GameController with ObserverGameMatchView:

    /**
     * Initializes the game by adding this controller as an observer to the view,
     * setting up the initial player, and drawing the first tile.
     */
    def initialize(): Unit =
      view.addObserver(this)
      model.initializeFirstPlayer()
      model.drawTile()

    /**
     * Places a tile on the game board at the specified position and sends available follower positions.
     *
     * @param gameTile the tile to place
     * @param position the position on the board to place the tile
     */
    def placeTile(gameTile: GameTile, position: Position): Unit =
      model.placeTile(gameTile, position)
      sendAvailableFollowerPositions(gameTile, position)

    /**
     * Places a follower on a specific segment of a tile at the given position.
     * If the placement is successful, proceeds to the next turn.
     *
     * @param position the position on the board where the follower is placed
     * @param segment the segment of the tile where the follower is placed
     * @param player the player placing the follower
     */
    def placeFollower(position: Position, segment: TileSegment, player: Player): Unit =
      if model.placeFollower(position, segment, player) then
        nextTurn()

    /**
     * Skips the follower placement and proceeds to the next turn.
     */
    def skipFollowerPlacement(): Unit =
      nextTurn()

    /**
     * Proceeds to the next turn by calculating the score, switching to the next player, and drawing a new tile.
     */
    def nextTurn(): Unit =
      model.calculateScore(false)
      model.nextPlayer()
      model.drawTile()

    /**
     * Sends available follower positions for the given tile and position to the model.
     *
     * @param gameTile the tile placed on the board
     * @param position the position on the board where the tile is placed
     */
    private def sendAvailableFollowerPositions(gameTile: GameTile, position: Position): Unit =
      model.sendAvailableFollowerPositions(gameTile, position)