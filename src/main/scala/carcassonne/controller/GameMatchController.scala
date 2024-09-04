package carcassonne.controller

import carcassonne.model.game.{GameMatch, Player}
import carcassonne.model.tile.{GameTile, TileSegment}
import carcassonne.observers.observers.ObserverGameMatchView
import carcassonne.util.Position
import carcassonne.view.GameMatchBoardView

trait Controller:
  def initialize(): Unit
  def placeTile(gameTile: GameTile, position: Position): Unit
  def placeFollower(gameTile: GameTile, segment: TileSegment, player: Player): Unit
  def nextTurn(): Unit

class GameMatchController(model: GameMatch, view: GameMatchBoardView) extends ObserverGameMatchView with Controller:

  def initialize(): Unit =
    view.addObserver(this)
    takeTurn()

  def placeTile(gameTile: GameTile, position: Position): Unit =
    model.placeTile(gameTile, position)

  def placeFollower(gameTile: GameTile, segment: TileSegment, player: Player): Unit = 
    if model.placeFollower(gameTile, segment, player) then
      nextTurn()
      
  def nextTurn(): Unit =
    model.nextPlayer()
    takeTurn()

  private def takeTurn(): Unit =
    if model.isDeckEmpty then
      gameEnded()
    else
      model.drawTile()

  private def gameEnded(): Unit =
    println("Game over! Final scores:")
    model.getPlayers.foreach(p => println(s"${p.name}: ${p.score}"))

