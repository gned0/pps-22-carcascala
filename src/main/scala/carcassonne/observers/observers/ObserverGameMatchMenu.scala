package carcassonne.observers.observers

import carcassonne.model.game.Player
import carcassonne.model.tile.GameTile

trait ObserverGameMatchMenu {
  
  private var currentPlayer: Player = _

  def setCurrentPlayer(player: Player): Unit =
    currentPlayer = player

  def getCurrentPlayer: Player = currentPlayer
  
  def tileDrawn(tileDrawn: GameTile): Unit

  def playerChanged(player: Player): Unit
}
