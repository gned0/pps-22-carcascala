package carcassonne.observers.observers.model

import carcassonne.model.game.Player
import carcassonne.model.tile.{GameTile, TileSegment}
import carcassonne.util.Position

trait ObserverGameMatchMenu:

  private var currentPlayer: Player = _

  def setCurrentPlayer(player: Player): Unit =
    currentPlayer = player

  def getCurrentPlayer: Player = currentPlayer

  def tileDrawn(tileDrawn: GameTile, tilesCount: Int): Unit

  def playerChanged(player: Player): Unit

  def availableFollowerPositions(availSegments: List[TileSegment], position: Position): Unit

  def updateScoreboard(scores: Map[Player, Int]): Unit
