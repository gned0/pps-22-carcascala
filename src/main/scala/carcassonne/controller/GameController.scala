package carcassonne.controller

import carcassonne.model.game.{GameState, Player}
import carcassonne.model.tile.{GameTile, TileSegment}
import carcassonne.observers.observers.ObserverGameMatchView
import carcassonne.util.Position
import carcassonne.view.GameMatchBoardView

trait GameController:
  def initialize(): Unit
  def placeTile(gameTile: GameTile, position: Position): Unit
  def placeFollower(gameTile: GameTile, segment: TileSegment, player: Player): Unit
  def nextTurn(): Unit

object GameController:
  def apply(model: GameState, view: GameMatchBoardView): GameController = new GameControllerImpl(model, view)

  private class GameControllerImpl(model: GameState, view: GameMatchBoardView) extends GameController with ObserverGameMatchView:
    def initialize(): Unit =
      view.addObserver(this)
      model.initializeFirstPlayer()
      takeTurn()

    def placeTile(gameTile: GameTile, position: Position): Unit =
      model.placeTile(gameTile, position)
      sendAvailableFollowerPositions(gameTile, position)

    def placeFollower(gameTile: GameTile, segment: TileSegment, player: Player): Unit =
      if model.placeFollower(gameTile, segment, player) then
        nextTurn()

    def skipFollowerPlacement(): Unit =
      nextTurn()
    
    def nextTurn(): Unit =
      model.calculateScore()
      model.nextPlayer()
      takeTurn()

    private def takeTurn(): Unit =
      if model.isDeckEmpty then
        gameEnded()
      else
        model.drawTile()

    private def gameEnded(): Unit =
      println("Game over! Final scores:")
      model.getPlayers.foreach(p => println(s"${p.name}: ${p.getScore}"))

    private def sendAvailableFollowerPositions(gameTile: GameTile, position: Position): Unit =
      model.sendAvailableFollowerPositions(gameTile, position)