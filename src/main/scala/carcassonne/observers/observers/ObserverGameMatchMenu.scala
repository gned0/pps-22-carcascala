package carcassonne.observers.observers

import carcassonne.model.tile.GameTile

trait ObserverGameMatchMenu {
  def tileDrawn(tileDrawn: GameTile): Unit
}
