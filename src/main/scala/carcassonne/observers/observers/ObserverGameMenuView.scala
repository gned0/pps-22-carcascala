package carcassonne.observers.observers

import carcassonne.model.tile.{GameTile, GameTileFactory}
import scalafx.scene.image.{Image, ImageView}

trait ObserverGameMenuView:
  
  private var _drawnTile: (GameTile, ImageView) = (GameTileFactory.createStartTile(),
    ImageView(
      new Image(getClass.getResource("../../tiles/" + GameTileFactory.createStartTile().imagePath).toExternalForm)
    )
  )
  
  def getDrawnTile: (GameTile, ImageView) = _drawnTile
  
  def setDrawnTile(drawnTile: (GameTile, ImageView)): Unit =
    _drawnTile = drawnTile

  def playerChanged(playerName: String): Unit
  
