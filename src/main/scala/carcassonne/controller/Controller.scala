package carcassonne.controller

import carcassonne.model.game.{GameMatch, Player}
import carcassonne.model.tile.{GameTile, TileSegment}
import carcassonne.util.Position
import carcassonne.view.GameMatchView

trait Controller:
  def initialize(): Unit
  def placeTile(gameTile: GameTile, position: Position): Unit
  def placeFollower(gameTile: GameTile, segment: TileSegment, player: Player): Unit
  def nextTurn(): Unit
  def terminate(): Unit


class GameController(model: GameMatch, view: GameMatchView) extends Controller:
    def initialize(): Unit = ???

    def placeTile(gameTile: GameTile, position: Position): Unit =
        model.placeTile(gameTile, position)

    def placeFollower(gameTile: GameTile, segment: TileSegment, player: Player): Unit =
        model.placeFollower(gameTile, segment, player)

    def nextTurn(): Unit = ???

    def terminate(): Unit =
        model.gameEnded()