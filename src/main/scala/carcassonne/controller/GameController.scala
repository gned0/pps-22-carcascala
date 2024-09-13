package carcassonne.controller

import carcassonne.model.game.{GameState, Player}
import carcassonne.model.tile.{GameTile, TileSegment}
import carcassonne.observers.observers.view.ObserverGameMatchView
import carcassonne.util.Position
import carcassonne.view.gameMatch.GameMatchBoardView

trait GameController:
  def initialize(): Unit
  def placeTile(gameTile: GameTile, position: Position): Unit
  def placeFollower(position: Position, segment: TileSegment, player: Player): Unit
  def nextTurn(): Unit

object GameController:
  def apply(model: GameState, view: GameMatchBoardView): GameController = new GameControllerImpl(model, view)

  private class GameControllerImpl(model: GameState, view: GameMatchBoardView) extends GameController with ObserverGameMatchView:
    def initialize(): Unit =
      view.addObserver(this)
      model.initializeFirstPlayer()
      model.drawTile()

    def placeTile(gameTile: GameTile, position: Position): Unit =
      model.placeTile(gameTile, position)
      sendAvailableFollowerPositions(gameTile, position)

    def placeFollower(position: Position, segment: TileSegment, player: Player): Unit =
      if model.placeFollower(position, segment, player) then
        nextTurn()

    def skipFollowerPlacement(): Unit =
      nextTurn()

    def nextTurn(): Unit =
      model.calculateScore(false)
      model.nextPlayer()
      model.drawTile()

    private def sendAvailableFollowerPositions(gameTile: GameTile, position: Position): Unit =
      model.sendAvailableFollowerPositions(gameTile, position)